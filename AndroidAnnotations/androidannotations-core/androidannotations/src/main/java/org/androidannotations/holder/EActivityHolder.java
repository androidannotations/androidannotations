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
import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.ReceiverRegistrationDelegate.IntentFilterData;
import org.androidannotations.internal.core.helper.ActivityIntentBuilder;
import org.androidannotations.internal.core.helper.IntentBuilder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
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

public class EActivityHolder extends EComponentWithViewSupportHolder implements HasIntentBuilder, HasExtras, HasInstanceState, HasOptionsMenu, HasOnActivityResult, HasReceiverRegistration,
		HasPreferenceHeaders {

	private ActivityIntentBuilder intentBuilder;
	private JMethod onCreate;
	private JMethod setIntent;
	private JMethod setContentViewLayout;
	private JVar initSavedInstanceParam;
	private JDefinedClass intentBuilderClass;
	private InstanceStateDelegate instanceStateDelegate;
	private OnActivityResultDelegate onActivityResultDelegate;
	private ReceiverRegistrationDelegate<EActivityHolder> receiverRegistrationDelegate;
	private PreferenceActivityDelegate preferencesHolder;
	private JMethod injectExtrasMethod;
	private JBlock injectExtrasBlock;
	private JVar injectExtras;
	private JBlock onCreateOptionsMenuMethodBody;
	private JVar onCreateOptionsMenuMenuInflaterVar;
	private JVar onCreateOptionsMenuMenuParam;
	private JVar onOptionsItemSelectedItem;
	private JVar onOptionsItemSelectedItemId;
	private JBlock onOptionsItemSelectedMiddleBlock;
	private NonConfigurationHolder nonConfigurationHolder;
	private JBlock initIfNonConfigurationNotNullBlock;
	private JVar initNonConfigurationInstance;
	private JMethod getLastNonConfigurationInstance;
	private JBlock onRetainNonConfigurationInstanceBindBlock;
	private JVar onRetainNonConfigurationInstance;
	private JBlock onStartBeforeSuperBlock;
	private JBlock onStartAfterSuperBlock;
	private JBlock onRestartBeforeSuperBlock;
	private JBlock onRestartAfterSuperBlock;
	private JBlock onResumeBeforeSuperBlock;
	private JBlock onResumeAfterSuperBlock;
	private JBlock onPauseBeforeSuperBlock;
	private JBlock onPauseAfterSuperBlock;
	private JMethod onStopMethod;
	private JBlock onStopBeforeSuperBlock;
	private JMethod onDestroyMethod;
	private JBlock onDestroyBeforeSuperBlock;
	private JBlock onDestroyAfterSuperBlock;
	private JMethod onNewIntentMethod;
	private JBlock onNewIntentAfterSuperBlock;
	private JBlock onConfigurationChangedBeforeSuperBlock;
	private JBlock onConfigurationChangedAfterSuperBlock;
	private JVar onConfigurationChangedNewConfigParam;
	private JMethod onContentChanged;
	private JBlock onContentChangedAfterSuperBlock;

	public EActivityHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement, AndroidManifest androidManifest) throws Exception {
		super(environment, annotatedElement);
		instanceStateDelegate = new InstanceStateDelegate(this);
		onActivityResultDelegate = new OnActivityResultDelegate(this);
		receiverRegistrationDelegate = new ReceiverRegistrationDelegate<>(this);
		preferencesHolder = new PreferenceActivityDelegate(this);
		setSetContentView();
		intentBuilder = new ActivityIntentBuilder(this, androidManifest);
		intentBuilder.build();
	}

	@Override
	protected void setContextRef() {
		contextRef = _this();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
		AbstractJClass bundleClass = getClasses().BUNDLE;
		initSavedInstanceParam = init.param(bundleClass, "savedInstanceState");
		getOnCreate();
	}

	public JMethod getOnCreate() {
		if (onCreate == null) {
			setOnCreate();
		}
		return onCreate;
	}

	public JMethod getSetIntent() {
		if (setIntent == null) {
			setSetIntent();
		}
		return setIntent;
	}

	private void setOnCreate() {
		onCreate = generatedClass.method(PUBLIC, getCodeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		AbstractJClass bundleClass = getClasses().BUNDLE;
		JVar onCreateSavedInstanceState = onCreate.param(bundleClass, "savedInstanceState");
		JBlock onCreateBody = onCreate.body();
		JVar previousNotifier = viewNotifierHelper.replacePreviousNotifier(onCreateBody);
		onCreateBody.invoke(getInit()).arg(onCreateSavedInstanceState);
		onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);
		viewNotifierHelper.resetPreviousNotifier(onCreateBody, previousNotifier);
	}

	// CHECKSTYLE:OFF

	private void setOnStart() {
		JMethod method = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onStart");
		method.annotate(Override.class);
		JBlock body = method.body();
		onStartBeforeSuperBlock = body.blockSimple();
		body.invoke(_super(), method);
		onStartAfterSuperBlock = body.blockSimple();
	}

	public JBlock getOnRestartAfterSuperBlock() {
		if (onRestartAfterSuperBlock == null) {
			setOnRestart();
		}
		return onRestartAfterSuperBlock;
	}

	private void setOnRestart() {
		JMethod method = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onRestart");
		method.annotate(Override.class);
		JBlock body = method.body();
		onRestartBeforeSuperBlock = body.blockSimple();
		body.invoke(_super(), method);
		onRestartAfterSuperBlock = body.blockSimple();
	}

	private void setOnResume() {
		JMethod method = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onResume");
		method.annotate(Override.class);
		JBlock body = method.body();
		onResumeBeforeSuperBlock = body.blockSimple();
		body.invoke(_super(), method);
		onResumeAfterSuperBlock = body.blockSimple();
	}

	private void setOnPause() {
		JMethod method = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onPause");
		method.annotate(Override.class);
		JBlock body = method.body();
		onPauseBeforeSuperBlock = body.blockSimple();
		body.invoke(_super(), method);
		onPauseAfterSuperBlock = body.blockSimple();
	}

	private void setOnNewIntent() {
		onNewIntentMethod = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onNewIntent");
		onNewIntentMethod.annotate(Override.class);
		JVar intent = onNewIntentMethod.param(getClasses().INTENT, "intent");
		JBlock body = onNewIntentMethod.body();
		body.invoke(_super(), onNewIntentMethod).arg(intent);
		onNewIntentAfterSuperBlock = body.blockSimple();
	}

	private void setSetIntent() {
		setIntent = generatedClass.method(PUBLIC, getCodeModel().VOID, "setIntent");
		setIntent.annotate(Override.class);
		JVar methodParam = setIntent.param(getClasses().INTENT, "newIntent");
		JBlock setIntentBody = setIntent.body();
		setIntentBody.invoke(_super(), setIntent).arg(methodParam);
	}

	public JMethod getOnStop() {
		if (onStopMethod == null) {
			setOnStop();
		}
		return onStopMethod;
	}

	private void setOnStop() {
		onStopMethod = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onStop");
		onStopMethod.annotate(Override.class);
		JBlock body = onStopMethod.body();
		onStopBeforeSuperBlock = body.blockSimple();
		body.invoke(_super(), onStopMethod);
	}

	public JMethod getOnDestroy() {
		if (onDestroyMethod == null) {
			setOnDestroy();
		}
		return onDestroyMethod;
	}

	private void setOnDestroy() {
		onDestroyMethod = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onDestroy");
		onDestroyMethod.annotate(Override.class);
		JBlock body = onDestroyMethod.body();
		onDestroyBeforeSuperBlock = body.blockSimple();
		body.invoke(_super(), onDestroyMethod);
		onDestroyAfterSuperBlock = body.blockSimple();
	}

	public JBlock getOnConfigurationChangedBeforeSuperBlock() {
		if (onConfigurationChangedBeforeSuperBlock == null) {
			setOnConfigurationChanged();
		}
		return onConfigurationChangedBeforeSuperBlock;
	}

	public JBlock getOnConfigurationChangedAfterSuperBlock() {
		if (onConfigurationChangedAfterSuperBlock == null) {
			setOnConfigurationChanged();
		}
		return onConfigurationChangedAfterSuperBlock;
	}

	public JVar getOnConfigurationChangedNewConfigParam() {
		if (onConfigurationChangedNewConfigParam == null) {
			setOnConfigurationChanged();
		}
		return onConfigurationChangedNewConfigParam;
	}

	private void setOnConfigurationChanged() {
		JMethod method = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onConfigurationChanged");
		method.annotate(Override.class);
		AbstractJClass configurationClass = getClasses().CONFIGURATION;
		onConfigurationChangedNewConfigParam = method.param(configurationClass, "newConfig");
		JBlock body = method.body();
		onConfigurationChangedBeforeSuperBlock = body.blockSimple();
		body.invoke(_super(), method).arg(onConfigurationChangedNewConfigParam);
		onConfigurationChangedAfterSuperBlock = body.blockSimple();
	}

	public JMethod getOnContentChanged() {
		if (onContentChanged == null) {
			setOnContentChanged();
		}
		return onContentChanged;
	}

	public JBlock getOnContentChangedAfterSuperBlock() {
		if (onContentChangedAfterSuperBlock == null) {
			setOnContentChanged();
		}
		return onContentChangedAfterSuperBlock;
	}

	private void setOnContentChanged() {
		onContentChanged = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onContentChanged");
		onContentChanged.annotate(Override.class);
		JBlock body = onContentChanged.body();
		body.invoke(_super(), onContentChanged);
		onContentChangedAfterSuperBlock = body.blockSimple();
	}

	private void setOnCreateOptionsMenu() {
		JMethod method = generatedClass.method(PUBLIC, getCodeModel().BOOLEAN, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onCreateOptionsMenuMenuParam = method.param(getClasses().MENU, "menu");
		onCreateOptionsMenuMenuInflaterVar = methodBody.decl(getClasses().MENU_INFLATER, "menuInflater", invoke("getMenuInflater"));
		onCreateOptionsMenuMethodBody = methodBody.blockSimple();
		methodBody._return(_super().invoke(method).arg(onCreateOptionsMenuMenuParam));
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
	protected void setFindNativeFragmentById() {
		JMethod method = generatedClass.method(PRIVATE, getClasses().FRAGMENT, "findNativeFragmentById");
		JVar idParam = method.param(getCodeModel().INT, "id");
		JBlock body = method.body();
		body._return(invoke("getFragmentManager").invoke("findFragmentById").arg(idParam));
		findNativeFragmentById = method;
	}

	@Override
	protected void setFindSupportFragmentById() {
		JMethod method = generatedClass.method(PRIVATE, getClasses().SUPPORT_V4_FRAGMENT, "findSupportFragmentById");
		JVar idParam = method.param(getCodeModel().INT, "id");
		JBlock body = method.body();
		body._return(invoke("getSupportFragmentManager").invoke("findFragmentById").arg(idParam));
		findSupportFragmentById = method;
	}

	@Override
	protected void setFindNativeFragmentByTag() {
		JMethod method = generatedClass.method(PRIVATE, getClasses().FRAGMENT, "findNativeFragmentByTag");
		JVar tagParam = method.param(getClasses().STRING, "tag");
		JBlock body = method.body();
		body._return(invoke("getFragmentManager").invoke("findFragmentByTag").arg(tagParam));
		findNativeFragmentByTag = method;
	}

	@Override
	protected void setFindSupportFragmentByTag() {
		JMethod method = generatedClass.method(PRIVATE, getClasses().SUPPORT_V4_FRAGMENT, "findSupportFragmentByTag");
		JVar tagParam = method.param(getClasses().STRING, "tag");
		JBlock body = method.body();
		body._return(invoke("getSupportFragmentManager").invoke("findFragmentByTag").arg(tagParam));
		findSupportFragmentByTag = method;
	}

	public JMethod getSetContentViewLayout() {
		if (setContentViewLayout == null) {
			setSetContentView();
		}
		return setContentViewLayout;
	}

	private void setSetContentView() {
		getOnCreate();

		AbstractJClass layoutParamsClass = getClasses().VIEW_GROUP_LAYOUT_PARAMS;

		setContentViewLayout = setContentViewMethod(new AbstractJType[] { getCodeModel().INT }, new String[] { "layoutResID" });
		setContentViewMethod(new AbstractJType[] { getClasses().VIEW, layoutParamsClass }, new String[] { "view", "params" });
		setContentViewMethod(new AbstractJType[] { getClasses().VIEW }, new String[] { "view" });
	}

	private JMethod setContentViewMethod(AbstractJType[] paramTypes, String[] paramNames) {
		JMethod method = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "setContentView");
		method.annotate(Override.class);

		List<JVar> params = new ArrayList<>();
		for (int i = 0; i < paramTypes.length; i++) {
			JVar param = method.param(paramTypes[i], paramNames[i]);
			params.add(param);
		}
		JBlock body = method.body();
		JInvocation superCall = body.invoke(JExpr._super(), method);
		for (JVar arg : params) {
			superCall.arg(arg);
		}
		viewNotifierHelper.invokeViewChanged(body);
		return method;
	}

	public JVar getInitSavedInstanceParam() {
		return initSavedInstanceParam;
	}

	@Override
	public IntentBuilder getIntentBuilder() {
		return intentBuilder;
	}

	@Override
	public void setIntentBuilderClass(JDefinedClass intentBuilderClass) {
		this.intentBuilderClass = intentBuilderClass;
	}

	@Override
	public JDefinedClass getIntentBuilderClass() {
		return intentBuilderClass;
	}

	// CHECKSTYLE:ON

	@Override
	public JMethod getInjectExtrasMethod() {
		if (injectExtrasMethod == null) {
			setInjectExtras();
		}
		return injectExtrasMethod;
	}

	@Override
	public JBlock getInjectExtrasBlock() {
		if (injectExtrasBlock == null) {
			setInjectExtras();
		}
		return injectExtrasBlock;
	}

	@Override
	public JVar getInjectExtras() {
		if (injectExtras == null) {
			setInjectExtras();
		}
		return injectExtras;
	}

	private void setInjectExtras() {
		injectExtrasMethod = generatedClass.method(PRIVATE, getCodeModel().VOID, "injectExtras" + generationSuffix());
		JBlock injectExtrasBody = injectExtrasMethod.body();
		injectExtras = injectExtrasBody.decl(getClasses().BUNDLE, "extras_", invoke("getIntent").invoke("getExtras"));
		injectExtrasBlock = injectExtrasBody._if(injectExtras.ne(_null()))._then();

		getSetIntent().body().invoke(injectExtrasMethod);
		getInitBodyInjectionBlock().invoke(injectExtrasMethod);
	}

	public JBlock getOnNewIntentAfterSuperBlock() {
		if (onNewIntentAfterSuperBlock == null) {
			setOnNewIntent();
		}
		return onNewIntentAfterSuperBlock;
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

	public NonConfigurationHolder getNonConfigurationHolder() throws JClassAlreadyExistsException {
		if (nonConfigurationHolder == null) {
			setNonConfigurationHolder();
		}
		return nonConfigurationHolder;
	}

	private void setNonConfigurationHolder() throws JClassAlreadyExistsException {
		nonConfigurationHolder = new NonConfigurationHolder(this);
	}

	public JBlock getInitIfNonConfigurationNotNullBlock() throws JClassAlreadyExistsException {
		if (initIfNonConfigurationNotNullBlock == null) {
			setInitNonConfigurationInstance();
		}
		return initIfNonConfigurationNotNullBlock;
	}

	public JVar getInitNonConfigurationInstance() throws JClassAlreadyExistsException {
		if (initNonConfigurationInstance == null) {
			setInitNonConfigurationInstance();
		}
		return initNonConfigurationInstance;
	}

	private void setInitNonConfigurationInstance() throws JClassAlreadyExistsException {
		JBlock initBody = getInitBodyInjectionBlock();
		JDefinedClass ncHolderClass = getNonConfigurationHolder().getGeneratedClass();
		initNonConfigurationInstance = initBody.decl(ncHolderClass, "nonConfigurationInstance", cast(ncHolderClass, _super().invoke(getGetLastNonConfigurationInstance())));
		initIfNonConfigurationNotNullBlock = initBody._if(initNonConfigurationInstance.ne(_null()))._then();
	}

	public JMethod getGetLastNonConfigurationInstance() throws JClassAlreadyExistsException {
		if (getLastNonConfigurationInstance == null) {
			setGetLastNonConfigurationInstance();
		}
		return getLastNonConfigurationInstance;
	}

	private void setGetLastNonConfigurationInstance() throws JClassAlreadyExistsException {
		AnnotationHelper annotationHelper = new AnnotationHelper(getEnvironment());
		TypeElement fragmentActivityTypeElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT_ACTIVITY);
		TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(generatedClass._extends().fullName());
		String getLastNonConfigurationInstanceName = "getLastNonConfigurationInstance";
		if (fragmentActivityTypeElement != null && annotationHelper.isSubtype(typeElement.asType(), fragmentActivityTypeElement.asType())) {
			getLastNonConfigurationInstanceName = "getLastCustomNonConfigurationInstance";
		}

		NonConfigurationHolder ncHolder = getNonConfigurationHolder();
		JDefinedClass ncHolderClass = ncHolder.getGeneratedClass();
		JFieldVar superNonConfigurationInstanceField = ncHolder.getSuperNonConfigurationInstanceField();

		getLastNonConfigurationInstance = generatedClass.method(PUBLIC, Object.class, getLastNonConfigurationInstanceName);
		getLastNonConfigurationInstance.annotate(Override.class);
		JBlock body = getLastNonConfigurationInstance.body();
		JVar nonConfigurationInstance = body.decl(ncHolderClass, "nonConfigurationInstance", cast(ncHolderClass, _super().invoke(getLastNonConfigurationInstance)));
		body._if(nonConfigurationInstance.eq(_null()))._then()._return(_null());
		body._return(nonConfigurationInstance.ref(superNonConfigurationInstanceField));
	}

	public JBlock getOnRetainNonConfigurationInstanceBindBlock() throws JClassAlreadyExistsException {
		if (onRetainNonConfigurationInstanceBindBlock == null) {
			setOnRetainNonConfigurationInstance();
		}
		return onRetainNonConfigurationInstanceBindBlock;
	}

	public JVar getOnRetainNonConfigurationInstance() throws JClassAlreadyExistsException {
		if (onRetainNonConfigurationInstance == null) {
			setOnRetainNonConfigurationInstance();
		}
		return onRetainNonConfigurationInstance;
	}

	private void setOnRetainNonConfigurationInstance() throws JClassAlreadyExistsException {
		AnnotationHelper annotationHelper = new AnnotationHelper(getEnvironment());
		TypeElement fragmentActivityTypeElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT_ACTIVITY);
		TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(generatedClass._extends().fullName());

		String onRetainNonConfigurationInstanceName = "onRetainNonConfigurationInstance";
		if (fragmentActivityTypeElement != null && annotationHelper.isSubtype(typeElement.asType(), fragmentActivityTypeElement.asType())) {
			onRetainNonConfigurationInstanceName = "onRetainCustomNonConfigurationInstance";
		}

		NonConfigurationHolder ncHolder = getNonConfigurationHolder();
		JDefinedClass ncHolderClass = ncHolder.getGeneratedClass();

		JMethod onRetainNonConfigurationInstanceMethod = generatedClass.method(PUBLIC, ncHolderClass, onRetainNonConfigurationInstanceName);
		onRetainNonConfigurationInstanceMethod.annotate(Override.class);
		JBlock methodBody = onRetainNonConfigurationInstanceMethod.body();
		onRetainNonConfigurationInstance = methodBody.decl(ncHolderClass, "nonConfigurationInstanceState_", _new(ncHolderClass));
		IJExpression superCall = _super().invoke(onRetainNonConfigurationInstanceMethod);
		methodBody.assign(onRetainNonConfigurationInstance.ref(ncHolder.getSuperNonConfigurationInstanceField()), superCall);
		onRetainNonConfigurationInstanceBindBlock = methodBody.blockSimple();
		methodBody._return(onRetainNonConfigurationInstance);
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

	public JBlock getOnActivityResultAfterSuperBlock() {
		return onActivityResultDelegate.getAfterSuperBlock();
	}

	public JVar getOnActivityResultRequestCodeParam() {
		return onActivityResultDelegate.getRequestCodeParam();
	}

	@Override
	public JMethod getOnActivityResultMethod() {
		return onActivityResultDelegate.getMethod();
	}

	public JBlock getOnDestroyAfterSuperBlock() {
		if (onDestroyAfterSuperBlock == null) {
			setOnDestroy();
		}
		return onDestroyAfterSuperBlock;
	}

	@Override
	public JBlock getOnResumeAfterSuperBlock() {
		if (onResumeAfterSuperBlock == null) {
			setOnResume();
		}
		return onResumeAfterSuperBlock;
	}

	@Override
	public JBlock getOnCreateAfterSuperBlock() {
		return getInitBody();
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
	public JBlock getOnPauseBeforeSuperBlock() {
		if (onPauseBeforeSuperBlock == null) {
			setOnPause();
		}
		return onPauseBeforeSuperBlock;
	}

	public JBlock getOnPauseAfterSuperBlock() {
		if (onPauseAfterSuperBlock == null) {
			setOnPause();
		}
		return onPauseAfterSuperBlock;
	}


	@Override
	public JBlock getOnAttachAfterSuperBlock() {
		return receiverRegistrationDelegate.getOnAttachAfterSuperBlock();
	}

	@Override
	public JBlock getOnDetachBeforeSuperBlock() {
		return receiverRegistrationDelegate.getOnDetachBeforeSuperBlock();
	}

	@Override
	public JFieldVar getIntentFilterField(IntentFilterData intentFilterData) {
		return receiverRegistrationDelegate.getIntentFilterField(intentFilterData);
	}

	@Override
	public JBlock getIntentFilterInitializationBlock(IntentFilterData intentFilterData) {
		return getInitBodyInjectionBlock();
	}

	@Override
	public JBlock getPreferenceScreenInitializationBlock() {
		return getOnCreate().body();
	}

	@Override
	public JBlock getAddPreferencesFromResourceInjectionBlock() {
		return preferencesHolder.getAddPreferencesFromResourceInjectionBlock();
	}

	@Override
	public JBlock getAddPreferencesFromResourceAfterInjectionBlock() {
		return preferencesHolder.getAddPreferencesFromResourceAfterInjectionBlock();
	}

	@Override
	public void assignFindPreferenceByKey(JFieldRef idRef, AbstractJClass preferenceClass, JFieldRef fieldRef) {
		preferencesHolder.assignFindPreferenceByKey(idRef, preferenceClass, fieldRef);
	}

	@Override
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, AbstractJClass preferenceClass) {
		return preferencesHolder.getFoundPreferenceHolder(idRef, preferenceClass);
	}

	@Override
	public boolean usingSupportV7Preference() {
		return preferencesHolder.usingSupportV7Preference();
	}

	public AbstractJClass getBasePreferenceClass() {
		return preferencesHolder.getBasePreferenceClass();
	}

	@Override
	public JBlock getOnBuildHeadersBlock() {
		return preferencesHolder.getOnBuildHeadersBlock();
	}

	@Override
	public JVar getOnBuildHeadersTargetParam() {
		return preferencesHolder.getOnBuildHeadersTargetParam();
	}

}
