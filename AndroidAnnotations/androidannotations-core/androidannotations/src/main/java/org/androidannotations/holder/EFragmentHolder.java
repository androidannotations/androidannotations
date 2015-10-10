/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.holder;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JExpr.ref;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver.RegisterAt;
import org.androidannotations.holder.ReceiverRegistrationDelegate.IntentFilterData;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class EFragmentHolder extends EComponentWithViewSupportHolder implements HasInstanceState, HasOptionsMenu, HasOnActivityResult, HasReceiverRegistration, HasPreferences {

	private JFieldVar contentView;
	private JBlock setContentViewBlock;
	private JVar inflater;
	private JVar container;
	private JDefinedClass fragmentBuilderClass;
	private AbstractJClass narrowBuilderClass;
	private JFieldRef fragmentArgumentsBuilderField;
	private JMethod injectArgsMethod;
	private JBlock injectArgsBlock;
	private JVar injectBundleArgs;
	private InstanceStateDelegate instanceStateDelegate;
	private OnActivityResultDelegate onActivityResultDelegate;
	private ReceiverRegistrationDelegate<EFragmentHolder> receiverRegistrationDelegate;
	private PreferencesDelegate preferencesDelegate;
	private JBlock onCreateOptionsMenuMethodBody;
	private JVar onCreateOptionsMenuMenuInflaterVar;
	private JVar onCreateOptionsMenuMenuParam;
	private JVar onOptionsItemSelectedItem;
	private JVar onOptionsItemSelectedItemId;
	private JBlock onOptionsItemSelectedMiddleBlock;
	private JBlock onCreateAfterSuperBlock;
	private JBlock onDestroyBeforeSuperBlock;
	private JBlock onStartAfterSuperBlock;
	private JBlock onStopBeforeSuperBlock;
	private JBlock onResumeAfterSuperBlock;
	private JBlock onPauseBeforeSuperBlock;
	private JBlock onAttachAfterSuperBlock;
	private JBlock onDetachBeforeSuperBlock;
	private JBlock onDestroyViewAfterSuperBlock;

	public EFragmentHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
		instanceStateDelegate = new InstanceStateDelegate(this);
		onActivityResultDelegate = new OnActivityResultDelegate(this);
		receiverRegistrationDelegate = new ReceiverRegistrationDelegate<>(this);
		preferencesDelegate = new PreferencesDelegate(this);
		setOnCreate();
		setOnViewCreated();
		setFragmentBuilder();
	}

	private void setOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, getCodeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JVar onCreateSavedInstanceState = onCreate.param(getClasses().BUNDLE, "savedInstanceState");
		JBlock onCreateBody = onCreate.body();

		JVar previousNotifier = viewNotifierHelper.replacePreviousNotifier(onCreateBody);
		setFindViewById();
		onCreateBody.invoke(getInit()).arg(onCreateSavedInstanceState);
		onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);
		onCreateAfterSuperBlock = onCreateBody.blockSimple();
		viewNotifierHelper.resetPreviousNotifier(onCreateBody, previousNotifier);
	}

	private void setOnViewCreated() {
		JMethod onViewCreated = generatedClass.method(PUBLIC, getCodeModel().VOID, "onViewCreated");
		onViewCreated.annotate(Override.class);
		JVar view = onViewCreated.param(getClasses().VIEW, "view");
		JVar savedInstanceState = onViewCreated.param(getClasses().BUNDLE, "savedInstanceState");
		JBlock onViewCreatedBody = onViewCreated.body();
		onViewCreatedBody.invoke(_super(), onViewCreated).arg(view).arg(savedInstanceState);
		viewNotifierHelper.invokeViewChanged(onViewCreatedBody);
	}

	private void setFindViewById() {
		JMethod findViewById = generatedClass.method(PUBLIC, getClasses().VIEW, "findViewById");
		findViewById.annotate(Override.class);

		JVar idParam = findViewById.param(getCodeModel().INT, "id");

		JBlock body = findViewById.body();

		JFieldVar contentView = getContentView();

		body._if(contentView.eq(_null())) //
			._then()._return(_null());

		body._return(contentView.invoke(findViewById).arg(idParam));
	}

	private void setFragmentBuilder() throws JClassAlreadyExistsException {
		fragmentBuilderClass = generatedClass._class(PUBLIC | STATIC, "FragmentBuilder" + generationSuffix());

		narrowBuilderClass = narrow(fragmentBuilderClass);

		codeModelHelper.generify(fragmentBuilderClass, annotatedElement);
		AbstractJClass superClass = getJClass(org.androidannotations.api.builder.FragmentBuilder.class);
		superClass = superClass.narrow(narrowBuilderClass, getAnnotatedClass());
		fragmentBuilderClass._extends(superClass);
		fragmentArgumentsBuilderField = ref("args");
		setFragmentBuilderBuild();
		setFragmentBuilderCreate();
	}

	private void setFragmentBuilderBuild() {
		JMethod method = fragmentBuilderClass.method(PUBLIC, generatedClass._extends(), "build");
		method.annotate(Override.class);
		JBlock body = method.body();

		AbstractJClass result = narrow(generatedClass);
		JVar fragment = body.decl(result, "fragment_", _new(result));
		body.invoke(fragment, "setArguments").arg(fragmentArgumentsBuilderField);
		body._return(fragment);
	}

	private void setFragmentBuilderCreate() {
		JMethod method = generatedClass.method(STATIC | PUBLIC, narrowBuilderClass, "builder");
		codeModelHelper.generify(method, annotatedElement);
		method.body()._return(_new(narrowBuilderClass));
	}

	private void setOnCreateOptionsMenu() {
		JMethod method = generatedClass.method(PUBLIC, getCodeModel().VOID, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onCreateOptionsMenuMenuParam = method.param(getClasses().MENU, "menu");
		onCreateOptionsMenuMenuInflaterVar = method.param(getClasses().MENU_INFLATER, "inflater");
		onCreateOptionsMenuMethodBody = methodBody.blockSimple();
		methodBody.invoke(_super(), method).arg(onCreateOptionsMenuMenuParam).arg(onCreateOptionsMenuMenuInflaterVar);

		getInitBody().invoke("setHasOptionsMenu").arg(JExpr.TRUE);
	}

	private void setOnOptionsItemSelected() {
		JMethod method = generatedClass.method(JMod.PUBLIC, getCodeModel().BOOLEAN, "onOptionsItemSelected");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onOptionsItemSelectedItem = method.param(getClasses().MENU_ITEM, "item");
		onOptionsItemSelectedItemId = methodBody.decl(getCodeModel().INT, "itemId_", onOptionsItemSelectedItem.invoke("getItemId"));
		onOptionsItemSelectedMiddleBlock = methodBody.blockSimple();

		methodBody._return(invoke(_super(), method).arg(onOptionsItemSelectedItem));
	}

	@Override
	protected void setContextRef() {
		contextRef = JExpr.invoke("getActivity");
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
		init.param(getClasses().BUNDLE, "savedInstanceState");
	}

	public JFieldVar getContentView() {
		if (contentView == null) {
			setContentViewRelatedMethods();
		}
		return contentView;
	}

	private void setContentViewRelatedMethods() {
		setContentView();
		setOnCreateView();
		setOnDestroyView();
	}

	private void setContentView() {
		contentView = generatedClass.field(PRIVATE, getClasses().VIEW, "contentView" + generationSuffix());
	}

	private void setOnCreateView() {
		JMethod onCreateView = generatedClass.method(PUBLIC, getClasses().VIEW, "onCreateView");
		onCreateView.annotate(Override.class);

		inflater = onCreateView.param(getClasses().LAYOUT_INFLATER, "inflater");
		container = onCreateView.param(getClasses().VIEW_GROUP, "container");

		JVar savedInstanceState = onCreateView.param(getClasses().BUNDLE, "savedInstanceState");

		boolean forceInjection = getAnnotatedElement().getAnnotation(EFragment.class).forceLayoutInjection();

		JBlock body = onCreateView.body();

		if (!forceInjection) {
			body.assign(contentView, _super().invoke(onCreateView).arg(inflater).arg(container).arg(savedInstanceState));
		}

		setContentViewBlock = body.blockSimple();

		body._return(contentView);
	}

	private void setOnDestroyView() {
		JMethod onDestroyView = generatedClass.method(PUBLIC, getCodeModel().VOID, "onDestroyView");
		onDestroyView.annotate(Override.class);
		JBlock body = onDestroyView.body();
		body.invoke(_super(), onDestroyView);
		body.assign(contentView, _null());
		onDestroyViewAfterSuperBlock = body.blockSimple();
	}

	private JBlock getOnDestroyViewAfterSuperBlock() {
		if (onDestroyViewAfterSuperBlock == null) {
			setContentViewRelatedMethods();
		}
		return onDestroyViewAfterSuperBlock;
	}

	@Override
	public void processViewById(JFieldRef idRef, AbstractJClass viewClass, JFieldRef fieldRef) {
		super.processViewById(idRef, viewClass, fieldRef);
		clearInjectedView(fieldRef);
	}

	private void clearInjectedView(JFieldRef fieldRef) {
		JBlock block = getOnDestroyViewAfterSuperBlock();
		block.assign(fieldRef, _null());
	}

	private void setOnStart() {
		JMethod onStart = generatedClass.method(PUBLIC, getCodeModel().VOID, "onStart");
		onStart.annotate(Override.class);
		JBlock onStartBody = onStart.body();
		onStartBody.invoke(_super(), onStart);
		onStartAfterSuperBlock = onStartBody.blockSimple();
	}

	private void setOnAttach() {
		JMethod onAttach = generatedClass.method(PUBLIC, getCodeModel().VOID, "onAttach");
		onAttach.annotate(Override.class);
		JVar activityParam = onAttach.param(getClasses().ACTIVITY, "activity");
		JBlock onAttachBody = onAttach.body();
		onAttachBody.invoke(_super(), onAttach).arg(activityParam);
		onAttachAfterSuperBlock = onAttachBody.blockSimple();
	}

	private void setOnResume() {
		JMethod onResume = generatedClass.method(PUBLIC, getCodeModel().VOID, "onResume");
		onResume.annotate(Override.class);
		JBlock onResumeBody = onResume.body();
		onResumeBody.invoke(_super(), onResume);
		onResumeAfterSuperBlock = onResumeBody.blockSimple();
	}

	private void setOnPause() {
		JMethod onPause = generatedClass.method(PUBLIC, getCodeModel().VOID, "onPause");
		onPause.annotate(Override.class);
		JBlock onPauseBody = onPause.body();
		onPauseBeforeSuperBlock = onPauseBody.blockSimple();
		onPauseBody.invoke(_super(), onPause);
	}

	private void setOnDetach() {
		JMethod onDetach = generatedClass.method(PUBLIC, getCodeModel().VOID, "onDetach");
		onDetach.annotate(Override.class);
		JBlock onDetachBody = onDetach.body();
		onDetachBeforeSuperBlock = onDetachBody.blockSimple();
		onDetachBody.invoke(_super(), onDetach);
	}

	private void setOnStop() {
		JMethod onStop = generatedClass.method(PUBLIC, getCodeModel().VOID, "onStop");
		onStop.annotate(Override.class);
		JBlock onStopBody = onStop.body();
		onStopBeforeSuperBlock = onStopBody.blockSimple();
		onStopBody.invoke(_super(), onStop);
	}

	private void setOnDestroy() {
		JMethod onDestroy = generatedClass.method(PUBLIC, getCodeModel().VOID, "onDestroy");
		onDestroy.annotate(Override.class);
		JBlock onDestroyBody = onDestroy.body();
		onDestroyBeforeSuperBlock = onDestroyBody.blockSimple();
		onDestroyBody.invoke(_super(), onDestroy);
	}

	public JBlock getSetContentViewBlock() {
		if (setContentViewBlock == null) {
			setOnCreateView();
		}
		return setContentViewBlock;
	}

	public JVar getInflater() {
		if (inflater == null) {
			setOnCreateView();
		}
		return inflater;
	}

	public JVar getContainer() {
		if (container == null) {
			setOnCreateView();
		}
		return container;
	}

	public JDefinedClass getBuilderClass() {
		return fragmentBuilderClass;
	}

	public JFieldRef getBuilderArgsField() {
		return fragmentArgumentsBuilderField;
	}

	public JMethod getInjectArgsMethod() {
		if (injectArgsMethod == null) {
			setInjectArgs();
		}
		return injectArgsMethod;
	}

	public JBlock getInjectArgsBlock() {
		if (injectArgsBlock == null) {
			setInjectArgs();
		}
		return injectArgsBlock;
	}

	public JVar getInjectBundleArgs() {
		if (injectBundleArgs == null) {
			setInjectArgs();
		}
		return injectBundleArgs;
	}

	private void setInjectArgs() {
		injectArgsMethod = generatedClass.method(PRIVATE, getCodeModel().VOID, "injectFragmentArguments" + generationSuffix());
		JBlock injectExtrasBody = injectArgsMethod.body();
		injectBundleArgs = injectExtrasBody.decl(getClasses().BUNDLE, "args_", invoke("getArguments"));
		injectArgsBlock = injectExtrasBody._if(injectBundleArgs.ne(_null()))._then();

		getInitBodyInjectionBlock().invoke(injectArgsMethod);
	}

	@Override
	public JBlock getSaveStateMethodBody() {
		return instanceStateDelegate.getSaveStateMethodBody();
	}

	@Override
	public JVar getSaveStateBundleParam() {
		return instanceStateDelegate.getSaveStateBundleParam();
	}

	@Override
	public JMethod getRestoreStateMethod() {
		return instanceStateDelegate.getRestoreStateMethod();
	}

	@Override
	public JVar getRestoreStateBundleParam() {
		return instanceStateDelegate.getRestoreStateBundleParam();
	}

	@Override
	public JBlock getOnCreateOptionsMenuMethodBody() {
		if (onCreateOptionsMenuMethodBody == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMethodBody;
	}

	@Override
	public JVar getOnCreateOptionsMenuMenuInflaterVar() {
		if (onCreateOptionsMenuMenuInflaterVar == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMenuInflaterVar;
	}

	@Override
	public JVar getOnCreateOptionsMenuMenuParam() {
		if (onCreateOptionsMenuMenuParam == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMenuParam;
	}

	@Override
	public JVar getOnOptionsItemSelectedItem() {
		if (onOptionsItemSelectedItem == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedItem;
	}

	@Override
	public JVar getOnOptionsItemSelectedItemId() {
		if (onOptionsItemSelectedItemId == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedItemId;
	}

	@Override
	public JBlock getOnOptionsItemSelectedMiddleBlock() {
		if (onOptionsItemSelectedMiddleBlock == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedMiddleBlock;
	}

	@Override
	public JBlock getOnActivityResultCaseBlock(int requestCode) {
		return onActivityResultDelegate.getCaseBlock(requestCode);
	}

	@Override
	public JVar getOnActivityResultDataParam() {
		return onActivityResultDelegate.getDataParam();
	}

	@Override
	public JVar getOnActivityResultResultCodeParam() {
		return onActivityResultDelegate.getResultCodeParam();
	}

	@Override
	public JMethod getOnActivityResultMethod() {
		return onActivityResultDelegate.getMethod();
	}

	@Override
	public JFieldVar getIntentFilterField(IntentFilterData intentFilterData) {
		return receiverRegistrationDelegate.getIntentFilterField(intentFilterData);
	}

	@Override
	public JBlock getOnCreateAfterSuperBlock() {
		if (onCreateAfterSuperBlock == null) {
			setOnCreate();
		}
		return onCreateAfterSuperBlock;
	}

	@Override
	public JBlock getOnDestroyBeforeSuperBlock() {
		if (onDestroyBeforeSuperBlock == null) {
			setOnDestroy();
		}
		return onDestroyBeforeSuperBlock;
	}

	@Override
	public JBlock getOnStartAfterSuperBlock() {
		if (onStartAfterSuperBlock == null) {
			setOnStart();
		}
		return onStartAfterSuperBlock;
	}

	@Override
	public JBlock getOnStopBeforeSuperBlock() {
		if (onStopBeforeSuperBlock == null) {
			setOnStop();
		}
		return onStopBeforeSuperBlock;
	}

	@Override
	public JBlock getOnResumeAfterSuperBlock() {
		if (onResumeAfterSuperBlock == null) {
			setOnResume();
		}
		return onResumeAfterSuperBlock;
	}

	@Override
	public JBlock getOnPauseBeforeSuperBlock() {
		if (onPauseBeforeSuperBlock == null) {
			setOnPause();
		}
		return onPauseBeforeSuperBlock;
	}

	@Override
	public JBlock getOnAttachAfterSuperBlock() {
		if (onAttachAfterSuperBlock == null) {
			setOnAttach();
		}
		return onAttachAfterSuperBlock;
	}

	@Override
	public JBlock getOnDetachBeforeSuperBlock() {
		if (onDetachBeforeSuperBlock == null) {
			setOnDetach();
		}
		return onDetachBeforeSuperBlock;
	}

	@Override
	public JBlock getIntentFilterInitializationBlock(IntentFilterData intentFilterData) {
		if (RegisterAt.OnAttachOnDetach.equals(intentFilterData.getRegisterAt())) {
			return getOnAttachAfterSuperBlock();
		}
		return getInitBodyInjectionBlock();
	}

	@Override
	public JBlock getPreferenceScreenInitializationBlock() {
		return getOnCreateAfterSuperBlock();
	}

	@Override
	public JBlock getAddPreferencesFromResourceInjectionBlock() {
		return preferencesDelegate.getAddPreferencesFromResourceInjectionBlock();
	}

	@Override
	public JBlock getAddPreferencesFromResourceAfterInjectionBlock() {
		return preferencesDelegate.getAddPreferencesFromResourceAfterInjectionBlock();
	}

	@Override
	public void assignFindPreferenceByKey(JFieldRef idRef, AbstractJClass preferenceClass, JFieldRef fieldRef) {
		preferencesDelegate.assignFindPreferenceByKey(idRef, preferenceClass, fieldRef);
	}

	@Override
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, AbstractJClass preferenceClass) {
		return preferencesDelegate.getFoundPreferenceHolder(idRef, preferenceClass);
	}

	@Override
	public boolean usingSupportV7Preference() {
		return preferencesDelegate.usingSupportV7Preference();
	}

	public AbstractJClass getBasePreferenceClass() {
		return preferencesDelegate.getBasePreferenceClass();
	}
}
