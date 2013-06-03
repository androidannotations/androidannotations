package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.helper.*;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.List;

import static com.sun.codemodel.JExpr.*;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

public class EActivityHolder extends EComponentHolder implements HasIntentBuilder, HasViewChanged, HasExtras, HasInstanceState, HasOptionsMenu {

	private ViewNotifierHelper viewNotifierHelper;
	private GreenDroidHelper greenDroidHelper;
	private JMethod onCreate;
	private JMethod setIntent;
	private JMethod setContentViewLayout;
	private JVar initSavedInstanceParam;
	private JDefinedClass intentBuilderClass;
	private JFieldVar intentField;
	private ViewChangedHolder viewChangedHolder;
	private JMethod findNativeFragmentById;
	private JMethod findSupportFragmentById;
	private JMethod findNativeFragmentByTag;
	private JMethod findSupportFragmentByTag;
    private InstanceStateHolder instanceStateHolder;
	private RoboGuiceHolder roboGuiceHolder;
	private JMethod injectExtrasMethod;
	private JBlock injectExtrasBlock;
	private JVar injectExtras;
	private JBlock onCreateOptionsMenuMethodBody;
	private JVar onCreateOptionsMenuMenuInflaterVar;
	private JVar onCreateOptionsMenuMenuParam;

	public EActivityHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		viewNotifierHelper = new ViewNotifierHelper(this);
        instanceStateHolder = new InstanceStateHolder(this);
		createIntentBuilder();
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

	protected void setOnStart() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onStart");
		method.annotate(Override.class);
		JBlock body = method.body();
		roboGuiceHolder.onStartBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		roboGuiceHolder.onStartAfterSuperBlock = body.block();
	}

	protected void setOnRestart() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onRestart");
		method.annotate(Override.class);
		JBlock body = method.body();
		roboGuiceHolder.onRestartBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		roboGuiceHolder.onRestartAfterSuperBlock = body.block();
	}

	protected void setOnResume() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onResume");
		method.annotate(Override.class);
		JBlock body = method.body();
		roboGuiceHolder.onResumeBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
		roboGuiceHolder.onResumeAfterSuperBlock = body.block();
	}

	protected void setOnPause() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onPause");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(_super(), method);
		roboGuiceHolder.onPauseAfterSuperBlock = body.block();
	}

	protected void setOnNewIntent() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onNewIntent");
		method.annotate(Override.class);
		JVar intent = method.param(classes().INTENT, "intent");
		JBlock body = method.body();
		body.invoke(_super(), method).arg(intent);
		roboGuiceHolder.onNewIntentAfterSuperBlock = body.block();
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
		roboGuiceHolder.onStopBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
	}

	protected void setOnDestroy() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onDestroy");
		method.annotate(Override.class);
		JBlock body = method.body();
		roboGuiceHolder.onDestroyBeforeSuperBlock = body.block();
		body.invoke(_super(), method);
	}

	protected void setOnConfigurationChanged() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onConfigurationChanged");
		method.annotate(Override.class);
		JClass configurationClass = classes().CONFIGURATION;
		JVar newConfig = method.param(configurationClass, "newConfig");
		roboGuiceHolder.newConfig = newConfig;
		JBlock body = method.body();
		roboGuiceHolder.currentConfig = body.decl(configurationClass, "currentConfig", JExpr.invoke("getResources").invoke("getConfiguration"));
		body.invoke(_super(), method).arg(newConfig);
		roboGuiceHolder.onConfigurationChangedAfterSuperBlock = body.block();
	}

	protected void setOnContentChanged() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onContentChanged");
		method.annotate(Override.class);
		JBlock body = method.body();
		body.invoke(_super(), method);
		roboGuiceHolder.onContentChangedAfterSuperBlock = body.block();
	}

	protected void setOnActivityResult() {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, "onActivityResult");
		method.annotate(Override.class);
		JVar requestCode = method.param(codeModel().INT, "requestCode");
		JVar resultCode = method.param(codeModel().INT, "resultCode");
		JVar data = method.param(classes().INTENT, "data");
		JBlock body = method.body();
		body.invoke(_super(), method).arg(requestCode).arg(resultCode).arg(data);
		roboGuiceHolder.onActivityResultAfterSuperBlock = body.block();
		roboGuiceHolder.requestCode = requestCode;
		roboGuiceHolder.resultCode = requestCode;
		roboGuiceHolder.data = data;
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

	private boolean usesActionBarSherlock() {
		return new ThirdPartyLibHelper(new AnnotationHelper(processingEnvironment())).usesActionBarSherlock(this);
	}

	@Override
	public ViewChangedHolder getOnViewChangedHolder() {
		if (viewChangedHolder == null) {
			setViewChangedHolder();
		}
		return viewChangedHolder;
	}

	private void setViewChangedHolder() {
		viewChangedHolder = ViewChangedHolder.createViewChangedHolder(this);
	}

	@Override
	public JMethod getFindNativeFragmentById() {
		if (findNativeFragmentById == null) {
			setFindNativeFragmentById();
		}
		return findNativeFragmentById;
	}

	private void setFindNativeFragmentById() {
		JMethod method = generatedClass.method(PRIVATE, classes().FRAGMENT, "findNativeFragmentById");
		JVar idParam = method.param(codeModel().INT, "id");
		JBlock body = method.body();
		body._return(invoke("getFragmentManager").invoke("findFragmentById").arg(idParam));
		findNativeFragmentById = method;
	}

	@Override
	public JMethod getFindSupportFragmentById() {
		if (findSupportFragmentById == null) {
			setFindSupportFragmentById();
		}
		return findSupportFragmentById;
	}

	private void setFindSupportFragmentById() {
		JMethod method = generatedClass.method(PRIVATE, classes().SUPPORT_V4_FRAGMENT, "findSupportFragmentById");
		JVar idParam = method.param(codeModel().INT, "id");
		JBlock body = method.body();
		body._return(invoke("getSupportFragmentManager").invoke("findFragmentById").arg(idParam));
		findSupportFragmentById = method;
	}

	@Override
	public JMethod getFindNativeFragmentByTag() {
		if (findNativeFragmentByTag == null) {
			setFindNativeFragmentByTag();
		}
		return findNativeFragmentByTag;
	}

	private void setFindNativeFragmentByTag() {
		JMethod method = generatedClass.method(PRIVATE, classes().FRAGMENT, "findNativeFragmentByTag");
		JVar tagParam = method.param(classes().STRING, "tag");
		JBlock body = method.body();
		body._return(invoke("getFragmentManager").invoke("findFragmentByTag").arg(tagParam));
		findNativeFragmentByTag = method;
	}

	@Override
	public JMethod getFindSupportFragmentByTag() {
		if (findSupportFragmentByTag == null) {
			setFindSupportFragmentByTag();
		}
		return findSupportFragmentByTag;
	}

	private void setFindSupportFragmentByTag() {
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
		String setContentViewMethodName;
		if (usesGreenDroid()) {
			setContentViewMethodName = "setActionBarContentView";
		} else {
			setContentViewMethodName = "setContentView";
		}

		JClass layoutParamsClass = classes().VIEW_GROUP_LAYOUT_PARAMS;

		setContentViewLayout = setContentViewMethod(setContentViewMethodName, new JType[] { codeModel().INT }, new String[] { "layoutResID" });
		setContentViewMethod(setContentViewMethodName, new JType[] { classes().VIEW, layoutParamsClass }, new String[] { "view", "params" });
		setContentViewMethod(setContentViewMethodName, new JType[] { classes().VIEW }, new String[] { "view" });
	}

	private JMethod setContentViewMethod(String setContentViewMethodName, JType[] paramTypes, String[] paramNames) {
		JMethod method = generatedClass.method(JMod.PUBLIC, codeModel().VOID, setContentViewMethodName);
		method.annotate(Override.class);

		ArrayList<JVar> params = new ArrayList<JVar>();
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

	private boolean usesGreenDroid() {
		if (greenDroidHelper == null) {
			greenDroidHelper = new GreenDroidHelper(processingEnvironment());
		}
		return greenDroidHelper.usesGreenDroid(annotatedElement);
	}
	private void createIntentBuilder() throws JClassAlreadyExistsException {
		new ActivityIntentBuilder(this).build();
	}

	private void handleBackPressed() {
		Element declaredOnBackPressedMethod = getOnBackPressedMethod(annotatedElement);
		if (declaredOnBackPressedMethod != null) {

			processHolder.generateApiClass(declaredOnBackPressedMethod, SdkVersionHelper.class);

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
				&& method.getParameters().size() == 0 //
				;
	}

	@Override
	public void setIntentBuilderClass(JDefinedClass intentBuilderClass) {
		this.intentBuilderClass = intentBuilderClass;
	}

	@Override
	public JDefinedClass getIntentBuilderClass() {
		return intentBuilderClass;
	}

	@Override
	public void setIntentField(JFieldVar intentField) {
		this.intentField = intentField;
	}

	@Override
	public JFieldVar getIntentField() {
		return intentField;
	}

	public RoboGuiceHolder getRoboGuiceHolder() {
		if (roboGuiceHolder == null) {
			roboGuiceHolder = new RoboGuiceHolder(this);
		}
		return roboGuiceHolder;
	}

	protected void  setScopeField() {
		roboGuiceHolder.scope  = getGeneratedClass().field(JMod.PRIVATE, classes().CONTEXT_SCOPE, "scope_");
	}

	protected void setEventManagerField() {
		roboGuiceHolder.eventManager = generatedClass.field(JMod.PRIVATE, classes().EVENT_MANAGER, "eventManager_");
	}

	public void setGetInjector() {
		JMethod method = generatedClass.method(JMod.PUBLIC, classes().INJECTOR, "getInjector");
		method.annotate(Override.class);
		JExpression castApplication = cast(classes().INJECTOR_PROVIDER, invoke("getApplication"));
		method.body()._return(castApplication.invoke("getInjector"));
		roboGuiceHolder.getInjector = method;
	}

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
		getInit().body().invoke(injectExtrasMethod);
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
}
