package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.ViewNotifierHelper;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;

import static com.sun.codemodel.JExpr.*;
import static com.sun.codemodel.JMod.*;

public abstract class EComponentWithViewSupportHolder extends EComponentHolder {

	private APTCodeModelHelper codeModelHelper;
	protected ViewNotifierHelper viewNotifierHelper;
	private JBlock onViewChangedBody;
	private JVar onViewChangedHasViewsParam;
	protected JMethod findNativeFragmentById;
	protected JMethod findSupportFragmentById;
	protected JMethod findNativeFragmentByTag;
	protected JMethod findSupportFragmentByTag;
	private HashMap<String, TextWatcherHolder> textWatcherHolders = new HashMap<String, TextWatcherHolder>();
	private HashMap<String, OnSeekBarChangeListenerHolder> onSeekBarChangeListenerHolders = new HashMap<String, OnSeekBarChangeListenerHolder>();

	public EComponentWithViewSupportHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		codeModelHelper = new APTCodeModelHelper();
		viewNotifierHelper = new ViewNotifierHelper(this);
	}

	public JBlock getOnViewChangedBody() {
		if (onViewChangedBody == null) {
			setOnViewChanged();
		}
		return onViewChangedBody;
	}

	public JVar getOnViewChangedHasViewsParam() {
		if (onViewChangedHasViewsParam == null) {
			setOnViewChanged();
		}
		return onViewChangedHasViewsParam;
	}

	protected void setOnViewChanged() {
		getGeneratedClass()._implements(OnViewChangedListener.class);
		JMethod onViewChanged = getGeneratedClass().method(PUBLIC, codeModel().VOID, "onViewChanged");
		onViewChanged.annotate(Override.class);
		onViewChangedBody = onViewChanged.body();
		onViewChangedHasViewsParam = onViewChanged.param(HasViews.class, "hasViews");
		JClass notifierClass = refClass(OnViewChangedNotifier.class);
		getInit().body().staticInvoke(notifierClass, "registerOnViewChangedListener").arg(_this());
	}

	public JInvocation findViewById(JFieldRef idRef) {
		JInvocation findViewById = invoke(getOnViewChangedHasViewsParam(), "findViewById");
		findViewById.arg(idRef);
		return findViewById;
	}

	public JMethod getFindNativeFragmentById() {
		if (findNativeFragmentById == null) {
			setFindNativeFragmentById();
		}
		return findNativeFragmentById;
	}

	protected void setFindNativeFragmentById() {
		findNativeFragmentById = getGeneratedClass().method(PRIVATE, classes().FRAGMENT, "findNativeFragmentById");
		JVar idParam = findNativeFragmentById.param(codeModel().INT, "id");

		JBlock body = findNativeFragmentById.body();

		body._if(getContextRef()._instanceof(classes().ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(classes().ACTIVITY, "activity_", cast(classes().ACTIVITY, getContextRef()));

		body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentById").arg(idParam));
	}

	public JMethod getFindSupportFragmentById() {
		if (findSupportFragmentById == null) {
			setFindSupportFragmentById();
		}
		return findSupportFragmentById;
	}

	protected void setFindSupportFragmentById() {
		findSupportFragmentById = getGeneratedClass().method(PRIVATE, classes().SUPPORT_V4_FRAGMENT, "findSupportFragmentById");
		JVar idParam = findSupportFragmentById.param(codeModel().INT, "id");

		JBlock body = findSupportFragmentById.body();

		body._if(getContextRef()._instanceof(classes().FRAGMENT_ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(classes().FRAGMENT_ACTIVITY, "activity_", cast(classes().FRAGMENT_ACTIVITY, getContextRef()));

		body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentById").arg(idParam));
	}

	public JMethod getFindNativeFragmentByTag() {
		if (findNativeFragmentByTag == null) {
			setFindNativeFragmentByTag();
		}
		return findNativeFragmentByTag;
	}

	protected void setFindNativeFragmentByTag() {
		findNativeFragmentByTag = getGeneratedClass().method(PRIVATE, classes().FRAGMENT, "findNativeFragmentByTag");
		JVar tagParam = findNativeFragmentByTag.param(classes().STRING, "tag");

		JBlock body = findNativeFragmentByTag.body();

		body._if(getContextRef()._instanceof(classes().ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(classes().ACTIVITY, "activity_", cast(classes().ACTIVITY, getContextRef()));

		body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentByTag").arg(tagParam));
	}

	public JMethod getFindSupportFragmentByTag() {
		if (findSupportFragmentByTag == null) {
			setFindSupportFragmentByTag();
		}
		return findSupportFragmentByTag;
	}

	protected void setFindSupportFragmentByTag() {
		findSupportFragmentByTag = getGeneratedClass().method(PRIVATE, classes().SUPPORT_V4_FRAGMENT, "findSupportFragmentByTag");
		JVar tagParam = findSupportFragmentByTag.param(classes().STRING, "tag");

		JBlock body = findSupportFragmentByTag.body();

		body._if(getContextRef()._instanceof(classes().FRAGMENT_ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(classes().FRAGMENT_ACTIVITY, "activity_", cast(classes().FRAGMENT_ACTIVITY, getContextRef()));

		body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentByTag").arg(tagParam));
	}

	public TextWatcherHolder getTextWatcherHolder(JFieldRef idRef, TypeMirror viewParameterType) {
		String idRefString = codeModelHelper.getIdStringFromIdFieldRef(idRef);
		TextWatcherHolder textWatcherHolder = textWatcherHolders.get(idRefString);
		if (textWatcherHolder == null) {
			textWatcherHolder = createTextWatcherHolder(idRef, viewParameterType);
			textWatcherHolders.put(idRefString, textWatcherHolder);
		}
		return textWatcherHolder;
	}

	private TextWatcherHolder createTextWatcherHolder(JFieldRef idRef, TypeMirror viewParameterType) {
		JDefinedClass onTextChangeListenerClass = codeModel().anonymousClass(classes().TEXT_WATCHER);
		JClass viewClass = classes().TEXT_VIEW;
		if (viewParameterType != null) {
			viewClass = refClass(viewParameterType.toString());
		}

		JBlock onViewChangedBody = getOnViewChangedBody().block();
		JVar viewVariable = onViewChangedBody.decl(FINAL, viewClass, "view", cast(viewClass, findViewById(idRef)));
		onViewChangedBody._if(viewVariable.ne(JExpr._null()))._then() //
							.invoke(viewVariable, "addTextChangedListener").arg(_new(onTextChangeListenerClass));

		return new TextWatcherHolder(this, viewVariable, onTextChangeListenerClass);
	}

	public OnSeekBarChangeListenerHolder getOnSeekBarChangeListenerHolder(JFieldRef idRef) {
		String idRefString = codeModelHelper.getIdStringFromIdFieldRef(idRef);
		OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder = onSeekBarChangeListenerHolders.get(idRefString);
		if (onSeekBarChangeListenerHolder == null) {
			onSeekBarChangeListenerHolder = createOnSeekBarChangeListenerHolder(idRef);
			onSeekBarChangeListenerHolders.put(idRefString, onSeekBarChangeListenerHolder);
		}
		return onSeekBarChangeListenerHolder;
	}

	private OnSeekBarChangeListenerHolder createOnSeekBarChangeListenerHolder(JFieldRef idRef) {
		JDefinedClass onSeekbarChangeListenerClass = codeModel().anonymousClass(classes().ON_SEEKBAR_CHANGE_LISTENER);
		JClass viewClass = classes().SEEKBAR;

		JBlock onViewChangedBody = getOnViewChangedBody().block();
		JVar viewVariable = onViewChangedBody.decl(FINAL, viewClass, "view", cast(viewClass, findViewById(idRef)));
		onViewChangedBody._if(viewVariable.ne(JExpr._null()))._then() //
				.invoke(viewVariable, "setOnSeekBarChangeListener").arg(_new(onSeekbarChangeListenerClass));

		return new OnSeekBarChangeListenerHolder(this, onSeekbarChangeListenerClass);
	}
}
