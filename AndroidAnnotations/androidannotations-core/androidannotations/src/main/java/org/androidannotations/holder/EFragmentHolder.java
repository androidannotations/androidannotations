/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
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

import static com.helger.jcodemodel.JExpr.FALSE;
import static com.helger.jcodemodel.JExpr.TRUE;
import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JExpr.cond;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JExpr.ref;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static com.helger.jcodemodel.JMod.VOLATILE;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver.RegisterAt;
import org.androidannotations.holder.ReceiverRegistrationDelegate.IntentFilterData;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class EFragmentHolder extends EComponentWithViewSupportHolder
		implements HasInstanceState, HasOptionsMenu, HasOnActivityResult, HasActivityLifecycleMethods, HasReceiverRegistration, HasPreferences {

	private JFieldVar contentView;
	private JFieldVar viewDestroyedField;
	private JMethod onCreateViewMethod;
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
	private JMethod onCreateOptionsMenuMethod;
	private JBlock onCreateOptionsMenuMethodBody;
	private JBlock onCreateOptionsMenuMethodInflateBody;
	private JVar onCreateOptionsMenuMenuInflaterVar;
	private JVar onCreateOptionsMenuMenuParam;
	private JMethod onOptionsItemSelectedMethod;
	private JVar onOptionsItemSelectedItem;
	private JVar onOptionsItemSelectedItemId;
	private JBlock onOptionsItemSelectedMiddleBlock;
	private JMethod onViewCreatedMethod;
	private JMethod findViewByIdMethod;
	private JMethod onCreateMethod;
	private JBlock onCreateAfterSuperBlock;
	private JMethod onDestroyMethod;
	private JBlock onDestroyBeforeSuperBlock;
	private JMethod onStartMethod;
	private JBlock onStartAfterSuperBlock;
	private JMethod onStopMethod;
	private JBlock onStopBeforeSuperBlock;
	private JMethod onResumeMethod;
	private JBlock onResumeAfterSuperBlock;
	private JMethod onPauseMethod;
	private JBlock onPauseBeforeSuperBlock;
	private JMethod onAttachMethod;
	private JBlock onAttachAfterSuperBlock;
	private JMethod onDetachMethod;
	private JBlock onDetachBeforeSuperBlock;
	private JMethod onDestroyViewMethod;
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
		onCreateMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onCreate");
		onCreateMethod.annotate(Override.class);
		JVar onCreateSavedInstanceState = onCreateMethod.param(getClasses().BUNDLE, "savedInstanceState");
		JBlock onCreateBody = onCreateMethod.body();

		JVar previousNotifier = viewNotifierHelper.replacePreviousNotifier(onCreateBody);
		onCreateBody.add(JExpr.invoke(getInit()).arg(onCreateSavedInstanceState));
		onCreateBody.add(_super().invoke(onCreateMethod).arg(onCreateSavedInstanceState));
		onCreateAfterSuperBlock = onCreateBody.blockSimple();
		viewNotifierHelper.resetPreviousNotifier(onCreateBody, previousNotifier);
	}

	private void setOnViewCreated() {
		JMethod onViewCreatedMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onViewCreated");
		onViewCreatedMethod.annotate(Override.class);
		JVar view = onViewCreatedMethod.param(getClasses().VIEW, "view");
		JVar savedInstanceState = onViewCreatedMethod.param(getClasses().BUNDLE, "savedInstanceState");
		JBlock onViewCreatedBody = onViewCreatedMethod.body();
		onViewCreatedBody.add(_super().invoke(onViewCreatedMethod).arg(view).arg(savedInstanceState));
		viewNotifierHelper.invokeViewChanged(onViewCreatedBody);
	}

	@Override
	public IJExpression getFindViewByIdExpression(JVar idParam) {
		JFieldVar contentView = getContentView();
		JInvocation invocation = contentView.invoke("findViewById").arg(idParam);
		return cond(contentView.eq(_null()), _null(), invocation);
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
		body.add(fragment.invoke("setArguments").arg(fragmentArgumentsBuilderField));
		body._return(fragment);
	}

	private void setFragmentBuilderCreate() {
		JMethod method = generatedClass.method(STATIC | PUBLIC, narrowBuilderClass, "builder");
		codeModelHelper.generify(method, annotatedElement);
		method.body()._return(_new(narrowBuilderClass));
	}

	private void setOnCreateOptionsMenu() {
		onCreateOptionsMenuMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onCreateOptionsMenu");
		onCreateOptionsMenuMethod.annotate(Override.class);
		JBlock methodBody = onCreateOptionsMenuMethod.body();
		onCreateOptionsMenuMenuParam = onCreateOptionsMenuMethod.param(getClasses().MENU, "menu");
		onCreateOptionsMenuMenuInflaterVar = onCreateOptionsMenuMethod.param(getClasses().MENU_INFLATER, "inflater");
		onCreateOptionsMenuMethodInflateBody = methodBody.blockSimple();
		onCreateOptionsMenuMethodBody = methodBody.blockSimple();
		methodBody.add(_super().invoke(onCreateOptionsMenuMethod).arg(onCreateOptionsMenuMenuParam).arg(onCreateOptionsMenuMenuInflaterVar));

		getInitBody().add(JExpr.invoke("setHasOptionsMenu").arg(JExpr.TRUE));
	}

	private void setOnOptionsItemSelected() {
		onOptionsItemSelectedMethod = generatedClass.method(JMod.PUBLIC, getCodeModel().BOOLEAN, "onOptionsItemSelected");
		onOptionsItemSelectedMethod.annotate(Override.class);
		JBlock methodBody = onOptionsItemSelectedMethod.body();
		onOptionsItemSelectedItem = onOptionsItemSelectedMethod.param(getClasses().MENU_ITEM, "item");
		onOptionsItemSelectedItemId = methodBody.decl(getCodeModel().INT, "itemId_", onOptionsItemSelectedItem.invoke("getItemId"));
		onOptionsItemSelectedMiddleBlock = methodBody.blockSimple();

		methodBody._return(invoke(_super(), onOptionsItemSelectedMethod).arg(onOptionsItemSelectedItem));
	}

	@Override
	protected void setContextRef() {
		contextRef = JExpr.invoke("getActivity");
	}

	@Override
	protected void setRootFragmentRef() {
		rootFragmentRef = _this();
	}

	@Override
	protected void setInit() {
		initMethod = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
		initMethod.param(getClasses().BUNDLE, "savedInstanceState");
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

	public JFieldVar getViewDestroyedField() {
		if (viewDestroyedField == null) {
			setViewDestroyedField();
		}
		return viewDestroyedField;
	}

	private void setViewDestroyedField() {
		viewDestroyedField = generatedClass.field(PRIVATE | VOLATILE, getCodeModel().BOOLEAN, "viewDestroyed" + generationSuffix(), TRUE);
		getSetContentViewBlock().assign(viewDestroyedField, FALSE);
		getOnDestroyViewAfterSuperBlock().assign(viewDestroyedField, TRUE);
	}

	private void setOnCreateView() {
		onCreateViewMethod = generatedClass.method(PUBLIC, getClasses().VIEW, "onCreateView");
		onCreateViewMethod.annotate(Override.class);

		inflater = onCreateViewMethod.param(getClasses().LAYOUT_INFLATER, "inflater");
		container = onCreateViewMethod.param(getClasses().VIEW_GROUP, "container");

		JVar savedInstanceState = onCreateViewMethod.param(getClasses().BUNDLE, "savedInstanceState");

		boolean forceInjection = getAnnotatedElement().getAnnotation(EFragment.class).forceLayoutInjection();

		JBlock body = onCreateViewMethod.body();

		if (!forceInjection) {
			body.assign(contentView, _super().invoke(onCreateViewMethod).arg(inflater).arg(container).arg(savedInstanceState));
		}

		setContentViewBlock = body.blockSimple();

		body._return(contentView);
	}

	private void setOnDestroyView() {
		onDestroyViewMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onDestroyView");
		onDestroyViewMethod.annotate(Override.class);
		JBlock body = onDestroyViewMethod.body();
		body.invoke(_super(), onDestroyViewMethod);
		body.assign(contentView, _null());
		onDestroyViewAfterSuperBlock = body.blockSimple();
	}

	public JMethod getOnDestroyView() {
		if (onDestroyViewMethod == null) {
			setContentViewRelatedMethods();
		}
		return onDestroyViewMethod;
	}
	
	public JBlock getOnDestroyViewAfterSuperBlock() {
		if (onDestroyViewAfterSuperBlock == null) {
			setContentViewRelatedMethods();
		}
		return onDestroyViewAfterSuperBlock;
	}

	public void clearInjectedView(IJAssignmentTarget fieldRef) {
		JBlock block = getOnDestroyViewAfterSuperBlock();
		block.assign(fieldRef, _null());
	}

	private void setOnStart() {
		onStartMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onStart");
		onStartMethod.annotate(Override.class);
		JBlock onStartBody = onStartMethod.body();
		onStartBody.invoke(_super(), onStartMethod);
		onStartAfterSuperBlock = onStartBody.blockSimple();
	}

	private void setOnAttach() {
		JMethod onAttachMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onAttach");
		onAttachMethod.annotate(Override.class);
		JVar activityParam = onAttachMethod.param(getClasses().ACTIVITY, "activity");
		JBlock onAttachBody = onAttachMethod.body();
		onAttachBody.add(_super().invoke(onAttachMethod).arg(activityParam));
		onAttachAfterSuperBlock = onAttachBody.blockSimple();
	}

	private void setOnResume() {
		onResumeMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onResume");
		onResumeMethod.annotate(Override.class);
		JBlock onResumeBody = onResumeMethod.body();
		onResumeBody.invoke(_super(), onResumeMethod);
		onResumeAfterSuperBlock = onResumeBody.blockSimple();
	}

	private void setOnPause() {
		onPauseMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onPause");
		onPauseMethod.annotate(Override.class);
		JBlock onPauseBody = onPauseMethod.body();
		onPauseBeforeSuperBlock = onPauseBody.blockSimple();
		onPauseBody.invoke(_super(), onPauseMethod);
	}

	private void setOnDetach() {
		onDetachMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onDetach");
		onDetachMethod.annotate(Override.class);
		JBlock onDetachBody = onDetachMethod.body();
		onDetachBeforeSuperBlock = onDetachBody.blockSimple();
		onDetachBody.invoke(_super(), onDetachMethod);
	}

	private void setOnStop() {
		onStopMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onStop");
		onStopMethod.annotate(Override.class);
		JBlock onStopBody = onStopMethod.body();
		onStopBeforeSuperBlock = onStopBody.blockSimple();
		onStopBody.invoke(_super(), onStopMethod);
	}

	private void setOnDestroy() {
		onDestroyMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onDestroy");
		onDestroyMethod.annotate(Override.class);
		JBlock onDestroyBody = onDestroyMethod.body();
		onDestroyBeforeSuperBlock = onDestroyBody.blockSimple();
		onDestroyBody.invoke(_super(), onDestroyMethod);
	}

	public JMethod getOnCreateView() {
		if (onCreateViewMethod == null) {
			setOnCreateView();
		}
		return onCreateViewMethod;
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
	public JBlock getRestoreStateMethodBody() {
		return instanceStateDelegate.getRestoreStateMethodBody();
	}

	@Override
	public JVar getRestoreStateBundleParam() {
		return instanceStateDelegate.getRestoreStateBundleParam();
	}

	public JMethod getOnCreateOptionsMenu() {
		if (onCreateOptionsMenuMethod == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMethod;
	}
	
	@Override
	public JBlock getOnCreateOptionsMenuMethodBody() {
		if (onCreateOptionsMenuMethodBody == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMethodBody;
	}

	@Override
	public JBlock getOnCreateOptionsMenuMethodInflateBody() {
		if (onCreateOptionsMenuMethodInflateBody == null) {
			setOnCreateOptionsMenu();
		}
		return onCreateOptionsMenuMethodInflateBody;
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

	public JMethod getOnOptionsItemSelected() {
		if (onOptionsItemSelectedMethod == null) {
			setOnOptionsItemSelected();
		}
		return onOptionsItemSelectedMethod;
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
	
	public JMethod getOnViewCreated() {
		if (onViewCreatedMethod == null) {
			setOnViewCreated();
		}
		return onViewCreatedMethod;
	}
	
	public JMethod getOnCreate() {
		if (onCreateMethod == null) {
			setOnCreate();
		}
		return onCreateMethod;
	}

	@Override
	public JBlock getStartLifecycleAfterSuperBlock() {
		return getOnCreateAfterSuperBlock();
	}

	@Override
	public JBlock getEndLifecycleBeforeSuperBlock() {
		return getOnDestroyBeforeSuperBlock();
	}

	@Override
	public JBlock getOnCreateAfterSuperBlock() {
		if (onCreateAfterSuperBlock == null) {
			setOnCreate();
		}
		return onCreateAfterSuperBlock;
	}

	public JMethod getOnDestroyMethod() {
		if (onDestroyMethod == null) {
			setOnDestroy();
		}
		return onDestroyMethod;
	}
	
	@Override
	public JBlock getOnDestroyBeforeSuperBlock() {
		if (onDestroyBeforeSuperBlock == null) {
			setOnDestroy();
		}
		return onDestroyBeforeSuperBlock;
	}

	public JMethod getOnStart() {
		if (onStartMethod == null) {
			setOnStart();
		}
		return onStartMethod;
	}
	
	@Override
	public JBlock getOnStartAfterSuperBlock() {
		if (onStartAfterSuperBlock == null) {
			setOnStart();
		}
		return onStartAfterSuperBlock;
	}

	public JMethod getOnStop() {
		if (onStopMethod == null) {
			setOnStop();
		}
		return onStopMethod;
	}
	
	@Override
	public JBlock getOnStopBeforeSuperBlock() {
		if (onStopBeforeSuperBlock == null) {
			setOnStop();
		}
		return onStopBeforeSuperBlock;
	}
	
	public JMethod getOnResumeMethod() {
		if (onResumeMethod == null) {
			setOnResume();
		}
		return onResumeMethod;
	}

	@Override
	public JBlock getOnResumeAfterSuperBlock() {
		if (onResumeAfterSuperBlock == null) {
			setOnResume();
		}
		return onResumeAfterSuperBlock;
	}

	public JMethod getOnPauseMethod() {
		if (onPauseMethod == null) {
			setOnPause();
		}
		return onPauseMethod;
	}
	
	@Override
	public JBlock getOnPauseBeforeSuperBlock() {
		if (onPauseBeforeSuperBlock == null) {
			setOnPause();
		}
		return onPauseBeforeSuperBlock;
	}

	public JMethod getOnAttach() {
		if (onAttachMethod == null) {
			setOnAttach();
		}
		return onAttachMethod;
	}

	public JBlock getOnAttachAfterSuperBlock() {
		if (onAttachAfterSuperBlock == null) {
			setOnAttach();
		}
		return onAttachAfterSuperBlock;
	}
	
	public void setOnDetachMethod(JMethod onDetachMethod) {
		if (onDetachMethod == null) {
			setOnDetach();
		}
		this.onDetachMethod = onDetachMethod;
	}

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
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, AbstractJClass preferenceClass) {
		return preferencesDelegate.getFoundPreferenceHolder(idRef, preferenceClass);
	}

	@Override
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, AbstractJClass preferenceClass, IJAssignmentTarget fieldRef) {
		return preferencesDelegate.getFoundPreferenceHolder(idRef, preferenceClass, fieldRef);
	}

	@Override
	public boolean usingSupportV7Preference() {
		return preferencesDelegate.usingSupportV7Preference();
	}

	@Override
	public boolean usingAndroidxPreference() {
		return preferencesDelegate.usingAndroidxPreference();
	}

	@Override
	public AbstractJClass getBasePreferenceClass() {
		return preferencesDelegate.getBasePreferenceClass();
	}
}
