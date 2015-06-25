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
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.helper.ViewNotifierHelper;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public abstract class EComponentWithViewSupportHolder extends EComponentHolder {

	protected ViewNotifierHelper viewNotifierHelper;
	private JMethod onViewChanged;
	private JBlock onViewChangedBody;
	private JBlock onViewChangedBodyBeforeFindViews;
	private JVar onViewChangedHasViewsParam;
	protected Map<String, FoundHolder> foundHolders = new HashMap<>();
	protected JMethod findNativeFragmentById;
	protected JMethod findSupportFragmentById;
	protected JMethod findNativeFragmentByTag;
	protected JMethod findSupportFragmentByTag;
	private Map<String, TextWatcherHolder> textWatcherHolders = new HashMap<>();
	private Map<String, OnSeekBarChangeListenerHolder> onSeekBarChangeListenerHolders = new HashMap<>();

	public EComponentWithViewSupportHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		viewNotifierHelper = new ViewNotifierHelper(this);
	}

	public JBlock getOnViewChangedBody() {
		if (onViewChangedBody == null) {
			setOnViewChanged();
		}
		return onViewChangedBody;
	}

	public JBlock getOnViewChangedBodyBeforeFindViews() {
		if (onViewChangedBodyBeforeFindViews == null) {
			setOnViewChanged();
		}
		return onViewChangedBodyBeforeFindViews;
	}

	public JVar getOnViewChangedHasViewsParam() {
		if (onViewChangedHasViewsParam == null) {
			setOnViewChanged();
		}
		return onViewChangedHasViewsParam;
	}

	protected void setOnViewChanged() {
		getGeneratedClass()._implements(OnViewChangedListener.class);
		onViewChanged = getGeneratedClass().method(PUBLIC, codeModel().VOID, "onViewChanged");
		onViewChanged.annotate(Override.class);
		onViewChangedBody = onViewChanged.body();
		onViewChangedBodyBeforeFindViews = onViewChangedBody.block();
		onViewChangedHasViewsParam = onViewChanged.param(HasViews.class, "hasViews");
		JClass notifierClass = refClass(OnViewChangedNotifier.class);
		getInitBody().staticInvoke(notifierClass, "registerOnViewChangedListener").arg(_this());
	}

	public JInvocation findViewById(JFieldRef idRef) {
		JInvocation findViewById = invoke(getOnViewChangedHasViewsParam(), "findViewById");
		findViewById.arg(idRef);
		return findViewById;
	}

	public void processViewById(JFieldRef idRef, JClass viewClass, JFieldRef fieldRef) {
		assignFindViewById(idRef, viewClass, fieldRef);
	}

	public void assignFindViewById(JFieldRef idRef, JClass viewClass, JFieldRef fieldRef) {
		String idRefString = codeModelHelper.getIdStringFromIdFieldRef(idRef);
		FoundViewHolder foundViewHolder = (FoundViewHolder) foundHolders.get(idRefString);

		JBlock block = getOnViewChangedBody();
		JExpression assignExpression;

		if (foundViewHolder != null) {
			assignExpression = foundViewHolder.getOrCastRef(viewClass);
		} else {
			assignExpression = findViewById(idRef);
			if (viewClass != null && viewClass != classes().VIEW) {
				assignExpression = cast(viewClass, assignExpression);

				if (viewClass.isParameterized()) {
					codeModelHelper.addSuppressWarnings(onViewChanged, "unchecked");
				}
			}
			foundHolders.put(idRefString, new FoundViewHolder(this, viewClass, fieldRef, block));
		}

		block.assign(fieldRef, assignExpression);
	}

	public FoundViewHolder getFoundViewHolder(JFieldRef idRef, JClass viewClass) {
		String idRefString = codeModelHelper.getIdStringFromIdFieldRef(idRef);
		FoundViewHolder foundViewHolder = (FoundViewHolder) foundHolders.get(idRefString);
		if (foundViewHolder == null) {
			foundViewHolder = createFoundViewAndIfNotNullBlock(idRef, viewClass);
			foundHolders.put(idRefString, foundViewHolder);
		}
		return foundViewHolder;
	}

	protected FoundViewHolder createFoundViewAndIfNotNullBlock(JFieldRef idRef, JClass viewClass) {
		JExpression findViewExpression = findViewById(idRef);
		JBlock block = getOnViewChangedBody().block();

		if (viewClass == null) {
			viewClass = classes().VIEW;
		} else if (viewClass != classes().VIEW) {
			findViewExpression = cast(viewClass, findViewExpression);
		}

		JVar view = block.decl(viewClass, "view", findViewExpression);
		return new FoundViewHolder(this, viewClass, view, block);
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
