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
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.helper.ActionBarSherlockHelper;
import org.androidannotations.helper.ActivityIntentBuilder;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IntentBuilder;
import org.androidannotations.helper.OrmLiteHelper;
import org.androidannotations.holder.ReceiverRegistrationHolder.IntentFilterData;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class EActivityHolder extends EComponentWithViewSupportHolder implements HasIntentBuilder, HasExtras, HasInstanceState, HasOptionsMenu, HasOnActivityResult, HasReceiverRegistration, HasPreferenceHeaders {

	private static final String ON_CONTENT_CHANGED_JAVADOC = "We cannot simply copy the " + "code from RoboActivity, because that can cause classpath issues. " + "For further details see issue #1116.";

	private ActivityIntentBuilder intentBuilder;
	private JMethod onCreate;
	private JMethod setIntent;
	private JMethod onNewIntentMethod;
	private JMethod setContentViewLayout;
	private JVar initSavedInstanceParam;
	private JDefinedClass intentBuilderClass;
	private InstanceStateHolder instanceStateHolder;
	private OnActivityResultHolder onActivityResultHolder;
	private ReceiverRegistrationHolder<EActivityHolder> receiverRegistrationHolder;
	private RoboGuiceHolder roboGuiceHolder;
	private PreferenceActivityHolder preferencesHolder;
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
	private JBlock onDestroyBeforeSuperBlock;
	private JBlock onDestroyAfterSuperBlock;
	private JBlock onResumeAfterSuperBlock;
	private JBlock onStartAfterSuperBlock;
	private JBlock onStopBeforeSuperBlock;
	private JBlock onPauseBeforeSuperBlock;

	public EActivityHolder(ProcessHolder processHolder, TypeElement annotatedElement, AndroidManifest androidManifest) throws Exception {
		super(processHolder, annotatedElement);
		instanceStateHolder = new InstanceStateHolder(this);
		onActivityResultHolder = new OnActivityResultHolder(this);
		receiverRegistrationHolder = new ReceiverRegistrationHolder<EActivityHolder>(this);
		preferencesHolder = new PreferenceActivityHolder(this);
		setSetContentView();
		intentBuilder = new ActivityIntentBuilder(this, androidManifest);
		intentBuilder.build();
		handleBackPressed();
	}

	@Override
	protected void setContextRef() {
		contextRef = _this();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
		JClass bundleClass = classes().BUNDLE;
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

	protected void setOnCreate() {
		onCreate = generatedClass.method(PUBLIC, codeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JClass bundleClass = classes().BUNDLE;
		JVar onCreateSavedInstanceState = onCreate.param(bundleClass, "savedInstanceState");
		JBlock onCreateBody = onCreate.body();
		JVar previousNotifier = viewNotifierHelper.replacePreviousNotifier(onCreateBody);
		onCreateBody.invoke(getInit()).arg(onCreateSavedInstanceState);
		onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);
		viewNotifierHelper.resetPreviousNotifier(onCreateBody, previousNotifier);
	}

	// CHECKSTYLE:OFF

	protected void setOnStart() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onStart");
		method.annotate(Override.class);
		JBlock body = method.body();
		getRoboGuiceHolder().onStartBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		onStartAfterSuperBlock = body.block();
	}

	protected void setOnRestart() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onRestart");
		method.annotate(Override.class);
		JBlock body = method.body();
		getRoboGuiceHolder().onRestartBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		getRoboGuiceHolder().onRestartAfterSuperBlock = body.block();
	}

	protected void setOnResume() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onResume");
		method.annotate(Override.class);
		JBlock body = method.body();
		getRoboGuiceHolder().onResumeBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		onResumeAfterSuperBlock = body.block();
	}

	protected void setOnPause() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onPause");
		method.annotate(Override.class);
		JBlock body = method.body();
		onPauseBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		getRoboGuiceHolder().onPauseAfterSuperBlock = body.block();
	}

	protected void setOnNewIntent() {
		onNewIntentMethod = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onNewIntent");
		onNewIntentMethod.annotate(Override.class);
		JVar intent = onNewIntentMethod.param(classes().INTENT, "intent");
		JBlock body = onNewIntentMethod.body();
		body.invoke(_super(), onNewIntentMethod).arg(intent);
		body.invoke(getSetIntent()).arg(intent);
		getRoboGuiceHolder().onNewIntentAfterSuperBlock = body.block();
	}

	private void setSetIntent() {
		setIntent = generatedClass.method(PUBLIC, codeModel().VOID, "setIntent");
		setIntent.annotate(Override.class);
		JVar methodParam = setIntent.param(classes().INTENT, "newIntent");
		JBlock setIntentBody = setIntent.body();
		setIntentBody.invoke(_super(), setIntent).arg(methodParam);
	}

	protected void setOnStop() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onStop");
		method.annotate(Override.class);
		JBlock body = method.body();
		onStopBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		getRoboGuiceHolder().onStop = method;
	}

	protected void setOnDestroy() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onDestroy");
		method.annotate(Override.class);
		JBlock body = method.body();
		getRoboGuiceHolder().onDestroy = method;
		onDestroyBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		onDestroyAfterSuperBlock = body.block();
	}

	protected void setOnConfigurationChanged() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onConfigurationChanged");
		method.annotate(Override.class);
		JClass configurationClass = classes().CONFIGURATION;
		JVar newConfig = method.param(configurationClass, "newConfig");
		getRoboGuiceHolder().newConfig = newConfig;
		JBlock body = method.body();
		getRoboGuiceHolder().currentConfig = body.decl(configurationClass, "currentConfig", JExpr.invoke("getResources").invoke("getConfiguration"));
		body.invoke(_super(), method).arg(newConfig);
		getRoboGuiceHolder().onConfigurationChangedAfterSuperBlock = body.block();
	}

	protected void setOnContentChanged() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onContentChanged");
		method.annotate(Override.class);
		method.javadoc().append(ON_CONTENT_CHANGED_JAVADOC);
		JBlock body = method.body();
		body.invoke(_super(), method);
		getRoboGuiceHolder().onContentChangedAfterSuperBlock = body.block();
	}

	private void setOnCreateOptionsMenu() {
		JClass menuClass = classes().MENU;
		JClass menuInflaterClass = classes().MENU_INFLATER;
		String getMenuInflaterMethodName = "getMenuInflater";
		if (usesActionBarSherlock()) {
			menuClass = classes().SHERLOCK_MENU;
			menuInflaterClass = classes().SHERLOCK_MENU_INFLATER;
			getMenuInflaterMethodName = "getSupportMenuInflater";
		}

		JMethod method = generatedClass.method(PUBLIC, codeModel().BOOLEAN, "onCreateOptionsMenu");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onCreateOptionsMenuMenuParam = method.param(menuClass, "menu");
		onCreateOptionsMenuMenuInflaterVar = methodBody.decl(menuInflaterClass, "menuInflater", invoke(getMenuInflaterMethodName));
		onCreateOptionsMenuMethodBody = methodBody.block();
		methodBody._return(_super().invoke(method).arg(onCreateOptionsMenuMenuParam));
	}

	private void setOnOptionsItemSelected() {
		JClass menuItemClass = classes().MENU_ITEM;
		if (usesActionBarSherlock()) {
			menuItemClass = classes().SHERLOCK_MENU_ITEM;
		}

		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().BOOLEAN, "onOptionsItemSelected");
		method.annotate(Override.class);
		JBlock methodBody = method.body();
		onOptionsItemSelectedItem = method.param(menuItemClass, "item");
		onOptionsItemSelectedItemId = methodBody.decl(codeModel().INT, "itemId_", onOptionsItemSelectedItem.invoke("getItemId"));
		onOptionsItemSelectedMiddleBlock = methodBody.block();

		methodBody._return(invoke(_super(), method).arg(onOptionsItemSelectedItem));
	}

	private boolean usesActionBarSherlock() {
		return new ActionBarSherlockHelper(new AnnotationHelper(processingEnvironment())).usesActionBarSherlock(this);
	}

	@Override
	protected void setFindNativeFragmentById() {
		JMethod method = generatedClass.method(PRIVATE, classes().FRAGMENT, "findNativeFragmentById");
		JVar idParam = method.param(codeModel().INT, "id");
		JBlock body = method.body();
		body._return(invoke("getFragmentManager").invoke("findFragmentById").arg(idParam));
		findNativeFragmentById = method;
	}

	@Override
	protected void setFindSupportFragmentById() {
		JMethod method = generatedClass.method(PRIVATE, classes().SUPPORT_V4_FRAGMENT, "findSupportFragmentById");
		JVar idParam = method.param(codeModel().INT, "id");
		JBlock body = method.body();
		body._return(invoke("getSupportFragmentManager").invoke("findFragmentById").arg(idParam));
		findSupportFragmentById = method;
	}

	@Override
	protected void setFindNativeFragmentByTag() {
		JMethod method = generatedClass.method(PRIVATE, classes().FRAGMENT, "findNativeFragmentByTag");
		JVar tagParam = method.param(classes().STRING, "tag");
		JBlock body = method.body();
		body._return(invoke("getFragmentManager").invoke("findFragmentByTag").arg(tagParam));
		findNativeFragmentByTag = method;
	}

	@Override
	protected void setFindSupportFragmentByTag() {
		JMethod method = generatedClass.method(PRIVATE, classes().SUPPORT_V4_FRAGMENT, "findSupportFragmentByTag");
		JVar tagParam = method.param(classes().STRING, "tag");
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

		JClass layoutParamsClass = classes().VIEW_GROUP_LAYOUT_PARAMS;

		setContentViewLayout = setContentViewMethod(new JType[] { codeModel().INT }, new String[] { "layoutResID" });
		setContentViewMethod(new JType[] { classes().VIEW, layoutParamsClass }, new String[] { "view", "params" });
		setContentViewMethod(new JType[] { classes().VIEW }, new String[] { "view" });
	}

	private JMethod setContentViewMethod(JType[] paramTypes, String[] paramNames) {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "setContentView");
		method.annotate(Override.class);

		List<JVar> params = new ArrayList<JVar>();
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

	private void handleBackPressed() {
		Element declaredOnBackPressedMethod = getOnBackPressedMethod(annotatedElement);
		if (declaredOnBackPressedMethod != null) {

			JMethod onKeyDownMethod = generatedClass.method(PUBLIC, codeModel().BOOLEAN, "onKeyDown");
			onKeyDownMethod.annotate(Override.class);
			JVar keyCodeParam = onKeyDownMethod.param(codeModel().INT, "keyCode");
			JClass keyEventClass = classes().KEY_EVENT;
			JVar eventParam = onKeyDownMethod.param(keyEventClass, "event");

			JClass versionHelperClass = refClass(SdkVersionHelper.class);

			JInvocation sdkInt = versionHelperClass.staticInvoke("getSdkInt");

			JBlock onKeyDownBody = onKeyDownMethod.body();

			onKeyDownBody._if( //
					sdkInt.lt(JExpr.lit(5)) //
							.cand(keyCodeParam.eq(keyEventClass.staticRef("KEYCODE_BACK"))) //
							.cand(eventParam.invoke("getRepeatCount").eq(JExpr.lit(0)))) //
					._then() //
					.invoke("onBackPressed");

			onKeyDownBody._return( //
					JExpr._super().invoke(onKeyDownMethod) //
							.arg(keyCodeParam) //
							.arg(eventParam));
		}
	}

	private ExecutableElement getOnBackPressedMethod(TypeElement activityElement) {

		AnnotationHelper annotationHelper = new AnnotationHelper(processingEnvironment());

		List<? extends Element> allMembers = annotationHelper.getElementUtils().getAllMembers(activityElement);

		List<ExecutableElement> activityInheritedMethods = ElementFilter.methodsIn(allMembers);

		for (ExecutableElement activityInheritedMethod : activityInheritedMethods) {
			if (isCustomOnBackPressedMethod(activityInheritedMethod)) {
				return activityInheritedMethod;
			}
		}
		return null;
	}

	private boolean isCustomOnBackPressedMethod(ExecutableElement method) {
		TypeElement methodClass = (TypeElement) method.getEnclosingElement();
		boolean methodBelongsToActivityClass = methodClass.getQualifiedName().toString().equals(CanonicalNameConstants.ACTIVITY);
		return !methodBelongsToActivityClass //
				&& method.getSimpleName().toString().equals("onBackPressed") //
				&& method.getThrownTypes().size() == 0 //
				&& method.getModifiers().contains(Modifier.PUBLIC) //
				&& method.getReturnType().getKind().equals(TypeKind.VOID) //
				&& method.getParameters().size() == 0;
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

	public RoboGuiceHolder getRoboGuiceHolder() {
		if (roboGuiceHolder == null) {
			roboGuiceHolder = new RoboGuiceHolder(this);
		}
		return roboGuiceHolder;
	}

	protected void setScopedObjectsField() {
		JClass keyWildCard = classes().KEY.narrow(codeModel().wildcard());
		JClass scopedHashMap = classes().HASH_MAP.narrow(keyWildCard, classes().OBJECT);

		getRoboGuiceHolder().scopedObjects = getGeneratedClass().field(JMod.PROTECTED, scopedHashMap, "scopedObjects" + GENERATION_SUFFIX);
		getRoboGuiceHolder().scopedObjects.assign(JExpr._new(scopedHashMap));
	}

	protected void setEventManagerField() {
		getRoboGuiceHolder().eventManager = generatedClass.field(JMod.PROTECTED, classes().EVENT_MANAGER, "eventManager" + GENERATION_SUFFIX);
	}

	protected void setContentViewListenerField() {
		getRoboGuiceHolder().contentViewListenerField = generatedClass.field(JMod.NONE, classes().CONTENT_VIEW_LISTENER, "ignored" + GENERATION_SUFFIX);
		getRoboGuiceHolder().contentViewListenerField.annotate(classes().INJECT);
	}

	protected void setScopeField() {
		getRoboGuiceHolder().scope = getGeneratedClass().field(JMod.PRIVATE, classes().CONTEXT_SCOPE, "scope_");
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
		injectExtrasMethod = generatedClass.method(PRIVATE, codeModel().VOID, "injectExtras_");
		JBlock injectExtrasBody = injectExtrasMethod.body();
		injectExtras = injectExtrasBody.decl(classes().BUNDLE, "extras_", invoke("getIntent").invoke("getExtras"));
		injectExtrasBlock = injectExtrasBody._if(injectExtras.ne(_null()))._then();

		getSetIntent().body().invoke(injectExtrasMethod);
		getInitBody().invoke(injectExtrasMethod);
	}

	public JMethod getOnNewIntent() {
		if (onNewIntentMethod == null) {
			setOnNewIntent();
		}
		return onNewIntentMethod;
	}

	@Override
	public JBlock getSaveStateMethodBody() {
		return instanceStateHolder.getSaveStateMethodBody();
	}

	@Override
	public JVar getSaveStateBundleParam() {
		return instanceStateHolder.getSaveStateBundleParam();
	}

	@Override
	public JMethod getRestoreStateMethod() {
		return instanceStateHolder.getRestoreStateMethod();
	}

	@Override
	public JVar getRestoreStateBundleParam() {
		return instanceStateHolder.getRestoreStateBundleParam();
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
		JBlock initBody = getInitBody();
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
		AnnotationHelper annotationHelper = new AnnotationHelper(processingEnvironment());
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
		AnnotationHelper annotationHelper = new AnnotationHelper(processingEnvironment());
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
		JExpression superCall = _super().invoke(onRetainNonConfigurationInstanceMethod);
		methodBody.assign(onRetainNonConfigurationInstance.ref(ncHolder.getSuperNonConfigurationInstanceField()), superCall);
		onRetainNonConfigurationInstanceBindBlock = methodBody.block();
		methodBody._return(onRetainNonConfigurationInstance);
	}

	@Override
	public JBlock getOnActivityResultCaseBlock(int requestCode) {
		return onActivityResultHolder.getCaseBlock(requestCode);
	}

	@Override
	public JVar getOnActivityResultDataParam() {
		return onActivityResultHolder.getDataParam();
	}

	@Override
	public JVar getOnActivityResultResultCodeParam() {
		return onActivityResultHolder.getResultCodeParam();
	}

	public JBlock getOnActivityResultAfterSuperBlock() {
		return onActivityResultHolder.getAfterSuperBlock();
	}

	public JVar getOnActivityResultRequestCodeParam() {
		return onActivityResultHolder.getRequestCodeParam();
	}

	@Override
	public JMethod getOnActivityResultMethod() {
		return onActivityResultHolder.getMethod();
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

	@Override
	public JBlock getOnAttachAfterSuperBlock() {
		return receiverRegistrationHolder.getOnAttachAfterSuperBlock();
	}

	@Override
	public JBlock getOnDetachBeforeSuperBlock() {
		return receiverRegistrationHolder.getOnDetachBeforeSuperBlock();
	}

	@Override
	public JFieldVar getIntentFilterField(IntentFilterData intentFilterData) {
		return receiverRegistrationHolder.getIntentFilterField(intentFilterData);
	}

	@Override
	public JBlock getIntentFilterInitializationBlock(IntentFilterData intentFilterData) {
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
		return getOnCreate().body();
	}

	@Override
	public JBlock getAddPreferencesFromResourceBlock() {
		return preferencesHolder.getAddPreferencesFromResourceBlock();
	}

	@Override
	public void assignFindPreferenceByKey(JFieldRef idRef, JClass preferenceClass, JFieldRef fieldRef) {
		preferencesHolder.assignFindPreferenceByKey(idRef, preferenceClass, fieldRef);
	}

	@Override
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, JClass preferenceClass) {
		return preferencesHolder.getFoundPreferenceHolder(idRef, preferenceClass);
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
