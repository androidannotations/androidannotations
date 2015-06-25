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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver.RegisterAt;
import org.androidannotations.helper.OrmLiteHelper;
import org.androidannotations.holder.ReceiverRegistrationDelegate.IntentFilterData;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JGenerifiable;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;

public class EFragmentHolder extends EComponentWithViewSupportHolder implements HasInstanceState, HasOptionsMenu, HasOnActivityResult, HasReceiverRegistration, HasPreferences {

	private JFieldVar contentView;
	private JBlock setContentViewBlock;
	private JVar inflater;
	private JVar container;
	private JDefinedClass fragmentBuilderClass;
	private JClass narrowBuilderClass;
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

	public EFragmentHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		instanceStateDelegate = new InstanceStateDelegate(this);
		onActivityResultDelegate = new OnActivityResultDelegate(this);
		receiverRegistrationDelegate = new ReceiverRegistrationDelegate<>(this);
		preferencesDelegate = new PreferencesDelegate(this);
		setOnCreate();
		setOnViewCreated();
		setFragmentBuilder();
	}

	private void setOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, codeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JVar onCreateSavedInstanceState = onCreate.param(classes().BUNDLE, "savedInstanceState");
		JBlock onCreateBody = onCreate.body();

		JVar previousNotifier = viewNotifierHelper.replacePreviousNotifier(onCreateBody);
		setFindViewById();
		onCreateBody.invoke(getInit()).arg(onCreateSavedInstanceState);
		onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);
		onCreateAfterSuperBlock = onCreateBody.block();
		viewNotifierHelper.resetPreviousNotifier(onCreateBody, previousNotifier);
	}

	private void setOnViewCreated() {
		JMethod onViewCreated = generatedClass.method(PUBLIC, codeModel().VOID, "onViewCreated");
		onViewCreated.annotate(Override.class);
		JVar view = onViewCreated.param(classes().VIEW, "view");
		JVar savedInstanceState = onViewCreated.param(classes().BUNDLE, "savedInstanceState");
		JBlock onViewCreatedBody = onViewCreated.body();
		onViewCreatedBody.invoke(_super(), onViewCreated).arg(view).arg(savedInstanceState);
		viewNotifierHelper.invokeViewChanged(onViewCreatedBody);
	}

	private void setFindViewById() {
		JMethod findViewById = generatedClass.method(PUBLIC, classes().VIEW, "findViewById");
		findViewById.annotate(Override.class);

		JVar idParam = findViewById.param(codeModel().INT, "id");

		JBlock body = findViewById.body();

		JFieldVar contentView = getContentView();

		body._if(contentView.eq(_null())) //
			._then()._return(_null());

		body._return(contentView.invoke(findViewById).arg(idParam));
	}

	private void setFragmentBuilder() throws JClassAlreadyExistsException {
		fragmentBuilderClass = generatedClass._class(PUBLIC | STATIC, "FragmentBuilder" + generationSuffix());

		narrowBuilderClass = narrow(fragmentBuilderClass);

		generify(fragmentBuilderClass);
		JClass superClass = refClass(org.androidannotations.api.builder.FragmentBuilder.class);
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

		JClass result = narrow(generatedClass);
		JVar fragment = body.decl(result, "fragment_", _new(result));
		body.invoke(fragment, "setArguments").arg(fragmentArgumentsBuilderField);
		body._return(fragment);
	}

	private void setFragmentBuilderCreate() {
		JMethod method = generatedClass.method(STATIC | PUBLIC, narrowBuilderClass, "builder");
		generify(method);
		method.body()._return(_new(narrowBuilderClass));
	}

	private void generify(JGenerifiable generifiable) {
		for (JTypeVar type : generatedClass.typeParams()) {
			generifiable.generify(type.name(), type._extends());
		}
	}

	private void setOnCreateOptionsMenu() {
		JMethod method = generatedClass.method(PUBLIC, codeModel().VOID, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onCreateOptionsMenuMenuParam = method.param(classes().MENU, "menu");
		onCreateOptionsMenuMenuInflaterVar = method.param(classes().MENU_INFLATER, "inflater");
		onCreateOptionsMenuMethodBody = methodBody.block();
		methodBody.invoke(_super(), method).arg(onCreateOptionsMenuMenuParam).arg(onCreateOptionsMenuMenuInflaterVar);

		getInitBody().invoke("setHasOptionsMenu").arg(JExpr.TRUE);
	}

	private void setOnOptionsItemSelected() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().BOOLEAN, "onOptionsItemSelected");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onOptionsItemSelectedItem = method.param(classes().MENU_ITEM, "item");
		onOptionsItemSelectedItemId = methodBody.decl(codeModel().INT, "itemId_", onOptionsItemSelectedItem.invoke("getItemId"));
		onOptionsItemSelectedMiddleBlock = methodBody.block();

		methodBody._return(invoke(_super(), method).arg(onOptionsItemSelectedItem));
	}

	@Override
	protected void setContextRef() {
		contextRef = JExpr.invoke("getActivity");
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init" + generationSuffix());
		init.param(classes().BUNDLE, "savedInstanceState");
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
		contentView = generatedClass.field(PRIVATE, classes().VIEW, "contentView" + generationSuffix());
	}

	private void setOnCreateView() {
		JMethod onCreateView = generatedClass.method(PUBLIC, classes().VIEW, "onCreateView");
		onCreateView.annotate(Override.class);

		inflater = onCreateView.param(classes().LAYOUT_INFLATER, "inflater");
		container = onCreateView.param(classes().VIEW_GROUP, "container");

		JVar savedInstanceState = onCreateView.param(classes().BUNDLE, "savedInstanceState");

		boolean forceInjection = getAnnotatedElement().getAnnotation(EFragment.class).forceLayoutInjection();

		JBlock body = onCreateView.body();

		if (!forceInjection) {
			body.assign(contentView, _super().invoke(onCreateView).arg(inflater).arg(container).arg(savedInstanceState));
		}

		setContentViewBlock = body.block();

		body._return(contentView);
	}

	private void setOnDestroyView() {
		JMethod onDestroyView = generatedClass.method(PUBLIC, codeModel().VOID, "onDestroyView");
		onDestroyView.annotate(Override.class);
		JBlock body = onDestroyView.body();
		body.invoke(_super(), onDestroyView);
		body.assign(contentView, _null());
		onDestroyViewAfterSuperBlock = body.block();
	}

	private JBlock getOnDestroyViewAfterSuperBlock() {
		if (onDestroyViewAfterSuperBlock == null) {
			setContentViewRelatedMethods();
		}
		return onDestroyViewAfterSuperBlock;
	}

	@Override
	public void processViewById(JFieldRef idRef, JClass viewClass, JFieldRef fieldRef) {
		super.processViewById(idRef, viewClass, fieldRef);
		clearInjectedView(fieldRef);
	}

	private void clearInjectedView(JFieldRef fieldRef) {
		JBlock block = getOnDestroyViewAfterSuperBlock();
		block.assign(fieldRef, _null());
	}

	private void setOnStart() {
		JMethod onStart = generatedClass.method(PUBLIC, codeModel().VOID, "onStart");
		onStart.annotate(Override.class);
		JBlock onStartBody = onStart.body();
		onStartBody.invoke(_super(), onStart);
		onStartAfterSuperBlock = onStartBody.block();
	}

	private void setOnAttach() {
		JMethod onAttach = generatedClass.method(PUBLIC, codeModel().VOID, "onAttach");
		onAttach.annotate(Override.class);
		JVar activityParam = onAttach.param(classes().ACTIVITY, "activity");
		JBlock onAttachBody = onAttach.body();
		onAttachBody.invoke(_super(), onAttach).arg(activityParam);
		onAttachAfterSuperBlock = onAttachBody.block();
	}

	private void setOnResume() {
		JMethod onResume = generatedClass.method(PUBLIC, codeModel().VOID, "onResume");
		onResume.annotate(Override.class);
		JBlock onResumeBody = onResume.body();
		onResumeBody.invoke(_super(), onResume);
		onResumeAfterSuperBlock = onResumeBody.block();
	}

	private void setOnPause() {
		JMethod onPause = generatedClass.method(PUBLIC, codeModel().VOID, "onPause");
		onPause.annotate(Override.class);
		JBlock onPauseBody = onPause.body();
		onPauseBeforeSuperBlock = onPauseBody.block();
		onPauseBody.invoke(_super(), onPause);
	}

	private void setOnDetach() {
		JMethod onDetach = generatedClass.method(PUBLIC, codeModel().VOID, "onDetach");
		onDetach.annotate(Override.class);
		JBlock onDetachBody = onDetach.body();
		onDetachBeforeSuperBlock = onDetachBody.block();
		onDetachBody.invoke(_super(), onDetach);
	}

	private void setOnStop() {
		JMethod onStop = generatedClass.method(PUBLIC, codeModel().VOID, "onStop");
		onStop.annotate(Override.class);
		JBlock onStopBody = onStop.body();
		onStopBeforeSuperBlock = onStopBody.block();
		onStopBody.invoke(_super(), onStop);
	}

	private void setOnDestroy() {
		JMethod onDestroy = generatedClass.method(PUBLIC, codeModel().VOID, "onDestroy");
		onDestroy.annotate(Override.class);
		JBlock onDestroyBody = onDestroy.body();
		onDestroyBeforeSuperBlock = onDestroyBody.block();
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
		injectArgsMethod = generatedClass.method(PRIVATE, codeModel().VOID, "injectFragmentArguments" + generationSuffix());
		JBlock injectExtrasBody = injectArgsMethod.body();
		injectBundleArgs = injectExtrasBody.decl(classes().BUNDLE, "args_", invoke("getArguments"));
		injectArgsBlock = injectExtrasBody._if(injectBundleArgs.ne(_null()))._then();

		getInitBody().invoke(injectArgsMethod);
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
		return getInitBody();
	}

	@Override
	protected JFieldVar setDatabaseHelperRef(TypeMirror databaseHelperTypeMirror) {
		JFieldVar databaseHelperRef = super.setDatabaseHelperRef(databaseHelperTypeMirror);
		OrmLiteHelper.injectReleaseInDestroy(databaseHelperRef, this, classes());

		return databaseHelperRef;
	}

	@Override
	public JBlock getPreferenceScreenInitializationBlock() {
		return getOnCreateAfterSuperBlock();
	}

	@Override
	public JBlock getAddPreferencesFromResourceBlock() {
		return preferencesDelegate.getAddPreferencesFromResourceBlock();
	}

	@Override
	public void assignFindPreferenceByKey(JFieldRef idRef, JClass preferenceClass, JFieldRef fieldRef) {
		preferencesDelegate.assignFindPreferenceByKey(idRef, preferenceClass, fieldRef);
	}

	@Override
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, JClass preferenceClass) {
		return preferencesDelegate.getFoundPreferenceHolder(idRef, preferenceClass);
	}
}
