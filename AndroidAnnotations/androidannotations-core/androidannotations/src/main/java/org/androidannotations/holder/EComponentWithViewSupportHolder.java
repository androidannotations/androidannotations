/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.api.bean.BeanHolder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.internal.helper.ViewNotifierHelper;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDirectClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JSwitch;
import com.helger.jcodemodel.JVar;

public abstract class EComponentWithViewSupportHolder extends EComponentHolder implements HasKeyEventCallbackMethods {

	protected ViewNotifierHelper viewNotifierHelper;
	private JMethod onViewChanged;
	private JBlock onViewChangedBody;
	private JBlock onViewChangedBodyInjectionBlock;
	private JBlock onViewChangedBodyViewHolderBlock;
	private JBlock onViewChangedBodyAfterInjectionBlock;
	private JBlock onViewChangedBodyBeforeInjectionBlock;
	private JVar onViewChangedHasViewsParam;
	protected Map<String, FoundHolder> foundHolders = new HashMap<>();
	protected DataBindingDelegate dataBindingDelegate;
	protected JMethod findNativeFragmentById;
	protected JMethod findSupportFragmentById;
	protected JMethod findNativeFragmentByTag;
	protected JMethod findSupportFragmentByTag;
	private Map<String, TextWatcherHolder> textWatcherHolders = new HashMap<>();
	private Map<String, OnSeekBarChangeListenerHolder> onSeekBarChangeListenerHolders = new HashMap<>();
	private Map<String, PageChangeHolder> pageChangeHolders = new HashMap<>();
	private KeyEventCallbackMethodsDelegate<EComponentWithViewSupportHolder> keyEventCallbackMethodsDelegate;

	public EComponentWithViewSupportHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
		viewNotifierHelper = new ViewNotifierHelper(this, environment);
		keyEventCallbackMethodsDelegate = new KeyEventCallbackMethodsDelegate<>(this);
		dataBindingDelegate = new DataBindingDelegate(this);
	}

	public IJExpression getFindViewByIdExpression(JVar idParam) {
		return _null();
	}

	public JBlock getOnViewChangedBody() {
		if (onViewChangedBody == null) {
			setOnViewChanged();
		}
		return onViewChangedBody;
	}

	public JBlock getOnViewChangedBodyBeforeInjectionBlock() {
		if (onViewChangedBodyBeforeInjectionBlock == null) {
			setOnViewChanged();
		}
		return onViewChangedBodyBeforeInjectionBlock;
	}

	public JBlock getOnViewChangedBodyInjectionBlock() {
		if (onViewChangedBodyInjectionBlock == null) {
			setOnViewChanged();
		}
		return onViewChangedBodyInjectionBlock;
	}

	public JBlock getOnViewChangedBodyViewHolderBlock() {
		if (onViewChangedBodyViewHolderBlock == null) {
			setOnViewChanged();
		}
		return onViewChangedBodyViewHolderBlock;
	}

	public JBlock getOnViewChangedBodyAfterInjectionBlock() {
		if (onViewChangedBodyAfterInjectionBlock == null) {
			setOnViewChanged();
		}
		return onViewChangedBodyAfterInjectionBlock;
	}

	public JVar getOnViewChangedHasViewsParam() {
		if (onViewChangedHasViewsParam == null) {
			setOnViewChanged();
		}
		return onViewChangedHasViewsParam;
	}

	protected void setOnViewChanged() {
		getGeneratedClass()._implements(OnViewChangedListener.class);
		onViewChanged = getGeneratedClass().method(PUBLIC, getCodeModel().VOID, "onViewChanged");
		onViewChanged.annotate(Override.class);
		onViewChangedBody = onViewChanged.body();
		onViewChangedBodyBeforeInjectionBlock = onViewChangedBody.blockVirtual();
		onViewChangedBodyViewHolderBlock = onViewChangedBody.blockVirtual();
		onViewChangedBodyInjectionBlock = onViewChangedBody.blockVirtual();
		onViewChangedBodyAfterInjectionBlock = onViewChangedBody.blockVirtual();
		onViewChangedHasViewsParam = onViewChanged.param(HasViews.class, "hasViews");
		AbstractJClass notifierClass = getJClass(OnViewChangedNotifier.class);
		getInitBodyInjectionBlock().staticInvoke(notifierClass, "registerOnViewChangedListener").arg(_this());
	}

	protected void implementBeanHolder() {
		getGeneratedClass()._implements(BeanHolder.class);
		JDirectClass genericType = getCodeModel().directClass("T");

		JFieldVar beansField = getGeneratedClass().field(PRIVATE | FINAL, getClasses().MAP.narrow(getClasses().CLASS.narrowAny(), getClasses().OBJECT), "beans_",
				_new(getClasses().HASH_MAP.narrow(getClasses().CLASS.narrowAny(), getClasses().OBJECT)));

		JMethod getBeanMethod = getGeneratedClass().method(PUBLIC, genericType, "getBean");
		getBeanMethod.generify("T");
		getBeanMethod.annotate(Override.class);

		JVar keyParam = getBeanMethod.param(getClasses().CLASS.narrow(genericType), "key");
		getBeanMethod.body()._return(cast(genericType, beansField.invoke("get").arg(keyParam)));

		JMethod putBeanMethod = getGeneratedClass().method(PUBLIC, getCodeModel().VOID, "putBean");
		putBeanMethod.generify("T");
		putBeanMethod.annotate(Override.class);

		keyParam = putBeanMethod.param(getClasses().CLASS.narrow(genericType), "key");
		JVar valueParam = putBeanMethod.param(genericType, "value");
		putBeanMethod.body().add(beansField.invoke("put").arg(keyParam).arg(valueParam));
	}

	public JInvocation findViewById(JFieldRef idRef) {
		JInvocation findViewById = invoke(getOnViewChangedHasViewsParam(), "internalFindViewById");
		findViewById.arg(idRef);
		return findViewById;
	}

	public FoundViewHolder getFoundViewHolder(JFieldRef idRef, AbstractJClass viewClass) {
		return getFoundViewHolder(idRef, viewClass, null);
	}

	public FoundViewHolder getFoundViewHolder(JFieldRef idRef, AbstractJClass viewClass, IJAssignmentTarget fieldRef) {
		String idRefString = idRef.name();
		FoundViewHolder foundViewHolder = (FoundViewHolder) foundHolders.get(idRefString);
		if (foundViewHolder == null) {
			foundViewHolder = createFoundViewAndIfNotNullBlock(idRef, viewClass, fieldRef);
			foundHolders.put(idRefString, foundViewHolder);
		}
		return foundViewHolder;
	}

	protected FoundViewHolder createFoundViewAndIfNotNullBlock(JFieldRef idRef, AbstractJClass viewClass, IJAssignmentTarget fieldRef) {
		IJExpression findViewExpression = findViewById(idRef);
		JBlock block = getOnViewChangedBodyBeforeInjectionBlock();

		if (viewClass == null) {
			viewClass = getClasses().VIEW;
		}

		IJAssignmentTarget foundView = fieldRef;
		if (foundView == null) {
			JVar view = block.decl(viewClass, "view_" + idRef.name(), findViewExpression);
			if (viewClass.isParameterized()) {
				codeModelHelper.addSuppressWarnings(view, "unchecked");
			}
			foundView = view;
		} else {
			block.add(foundView.assign(findViewExpression));
		}
		return new FoundViewHolder(this, viewClass, foundView, getOnViewChangedBodyViewHolderBlock());
	}

	public JMethod getFindNativeFragmentById() {
		if (findNativeFragmentById == null) {
			setFindNativeFragmentById();
		}
		return findNativeFragmentById;
	}

	protected void setFindNativeFragmentById() {
		findNativeFragmentById = getGeneratedClass().method(PRIVATE, getClasses().FRAGMENT, "findNativeFragmentById");
		JVar idParam = findNativeFragmentById.param(getCodeModel().INT, "id");

		JBlock body = findNativeFragmentById.body();

		body._if(getContextRef()._instanceof(getClasses().ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(getClasses().ACTIVITY, "activity_", cast(getClasses().ACTIVITY, getContextRef()));

		body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentById").arg(idParam));
	}

	public JMethod getFindSupportFragmentById() {
		if (findSupportFragmentById == null) {
			setFindSupportFragmentById();
		}
		return findSupportFragmentById;
	}

	protected void setFindSupportFragmentById() {
		if (getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.ANDROIDX_FRAGMENT) == null) {
			findSupportFragmentById = getGeneratedClass().method(PRIVATE, getClasses().SUPPORT_V4_FRAGMENT, "findSupportFragmentById");
		} else {
			findSupportFragmentById = getGeneratedClass().method(PRIVATE, getClasses().ANDROIDX_FRAGMENT, "findSupportFragmentById");
		}
		JVar idParam = findSupportFragmentById.param(getCodeModel().INT, "id");

		JBlock body = findSupportFragmentById.body();

		AbstractJClass fragmentActivity = getFragmentActivity();
		body._if(getContextRef()._instanceof(fragmentActivity).not())._then()._return(_null());

		JVar activityVar = body.decl(fragmentActivity, "activity_", cast(fragmentActivity, getContextRef()));

		body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentById").arg(idParam));
	}

	private AbstractJClass getFragmentActivity() {
		Elements elementUtils = getProcessingEnvironment().getElementUtils();
		if (elementUtils.getTypeElement(CanonicalNameConstants.ANDROIDX_FRAGMENT_ACTIVITY) != null) {
			return getClasses().ANDROIDX_FRAGMENT_ACTIVITY;
		} else {
			return getClasses().FRAGMENT_ACTIVITY;
		}
	}

	public JMethod getFindNativeFragmentByTag() {
		if (findNativeFragmentByTag == null) {
			setFindNativeFragmentByTag();
		}
		return findNativeFragmentByTag;
	}

	protected void setFindNativeFragmentByTag() {
		findNativeFragmentByTag = getGeneratedClass().method(PRIVATE, getClasses().FRAGMENT, "findNativeFragmentByTag");
		JVar tagParam = findNativeFragmentByTag.param(getClasses().STRING, "tag");

		JBlock body = findNativeFragmentByTag.body();

		body._if(getContextRef()._instanceof(getClasses().ACTIVITY).not())._then()._return(_null());

		JVar activityVar = body.decl(getClasses().ACTIVITY, "activity_", cast(getClasses().ACTIVITY, getContextRef()));

		body._return(activityVar.invoke("getFragmentManager").invoke("findFragmentByTag").arg(tagParam));
	}

	public JMethod getFindSupportFragmentByTag() {
		if (findSupportFragmentByTag == null) {
			setFindSupportFragmentByTag();
		}
		return findSupportFragmentByTag;
	}

	protected void setFindSupportFragmentByTag() {
		if (getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.ANDROIDX_FRAGMENT) == null) {
			findSupportFragmentByTag = getGeneratedClass().method(PRIVATE, getClasses().SUPPORT_V4_FRAGMENT, "findSupportFragmentByTag");
		} else {
			findSupportFragmentByTag = getGeneratedClass().method(PRIVATE, getClasses().ANDROIDX_FRAGMENT, "findSupportFragmentByTag");
		}
		JVar tagParam = findSupportFragmentByTag.param(getClasses().STRING, "tag");

		JBlock body = findSupportFragmentByTag.body();

		AbstractJClass fragmentActivity = getFragmentActivity();
		body._if(getContextRef()._instanceof(fragmentActivity).not())._then()._return(_null());

		JVar activityVar = body.decl(fragmentActivity, "activity_", cast(fragmentActivity, getContextRef()));

		body._return(activityVar.invoke("getSupportFragmentManager").invoke("findFragmentByTag").arg(tagParam));
	}

	public TextWatcherHolder getTextWatcherHolder(JFieldRef idRef, TypeMirror viewParameterType) {
		String idRefString = idRef.name();
		TextWatcherHolder textWatcherHolder = textWatcherHolders.get(idRefString);
		if (textWatcherHolder == null) {
			textWatcherHolder = createTextWatcherHolder(idRef, viewParameterType);
			textWatcherHolders.put(idRefString, textWatcherHolder);
		}
		return textWatcherHolder;
	}

	private TextWatcherHolder createTextWatcherHolder(JFieldRef idRef, TypeMirror viewParameterType) {
		JDefinedClass onTextChangeListenerClass = getCodeModel().anonymousClass(getClasses().TEXT_WATCHER);
		AbstractJClass viewClass = getClasses().TEXT_VIEW;
		if (viewParameterType != null) {
			viewClass = getJClass(viewParameterType.toString());
		}

		JBlock onViewChangedBody = getOnViewChangedBodyInjectionBlock().blockSimple();
		JVar viewVariable = onViewChangedBody.decl(FINAL, viewClass, "view", cast(viewClass, findViewById(idRef)));
		onViewChangedBody._if(viewVariable.ne(JExpr._null()))._then() //
				.invoke(viewVariable, "addTextChangedListener").arg(_new(onTextChangeListenerClass));

		return new TextWatcherHolder(this, viewVariable, onTextChangeListenerClass);
	}

	public OnSeekBarChangeListenerHolder getOnSeekBarChangeListenerHolder(JFieldRef idRef) {
		String idRefString = idRef.name();
		OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder = onSeekBarChangeListenerHolders.get(idRefString);
		if (onSeekBarChangeListenerHolder == null) {
			onSeekBarChangeListenerHolder = createOnSeekBarChangeListenerHolder(idRef);
			onSeekBarChangeListenerHolders.put(idRefString, onSeekBarChangeListenerHolder);
		}
		return onSeekBarChangeListenerHolder;
	}

	private OnSeekBarChangeListenerHolder createOnSeekBarChangeListenerHolder(JFieldRef idRef) {
		JDefinedClass onSeekbarChangeListenerClass = getCodeModel().anonymousClass(getClasses().ON_SEEKBAR_CHANGE_LISTENER);
		AbstractJClass viewClass = getClasses().SEEKBAR;

		FoundViewHolder foundViewHolder = getFoundViewHolder(idRef, viewClass);
		foundViewHolder.getIfNotNullBlock().invoke(foundViewHolder.getRef(), "setOnSeekBarChangeListener").arg(_new(onSeekbarChangeListenerClass));

		return new OnSeekBarChangeListenerHolder(this, onSeekbarChangeListenerClass);
	}

	public PageChangeHolder getPageChangeHolder(JFieldRef idRef, TypeMirror viewParameterType, boolean hasAddOnPageChangeListenerMethod) {
		String idRefString = idRef.name();
		PageChangeHolder pageChangeHolder = pageChangeHolders.get(idRefString);
		if (pageChangeHolder == null) {
			pageChangeHolder = createPageChangeHolder(idRef, viewParameterType, hasAddOnPageChangeListenerMethod);
			pageChangeHolders.put(idRefString, pageChangeHolder);
		}
		return pageChangeHolder;
	}

	private PageChangeHolder createPageChangeHolder(JFieldRef idRef, TypeMirror viewParameterType, boolean hasAddOnPageChangeListenerMethod) {
		AbstractJClass viewClass;
		JDefinedClass onPageChangeListenerClass;
		if (getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.ANDROIDX_VIEW_PAGER) == null) {
			viewClass = getClasses().VIEW_PAGER;
			onPageChangeListenerClass = getCodeModel().anonymousClass(getClasses().PAGE_CHANGE_LISTENER);
		} else {
			viewClass = getClasses().ANDROIDX_VIEW_PAGER;
			onPageChangeListenerClass = getCodeModel().anonymousClass(getClasses().ANDROIDX_PAGE_CHANGE_LISTENER);
		}
		if (viewParameterType != null) {
			viewClass = getJClass(viewParameterType.toString());
		}
		JBlock onViewChangedBody = getOnViewChangedBodyInjectionBlock().blockSimple();
		JVar viewVariable = onViewChangedBody.decl(FINAL, viewClass, "view", cast(viewClass, findViewById(idRef)));
		JBlock block = onViewChangedBody._if(viewVariable.ne(JExpr._null()))._then();
		if (hasAddOnPageChangeListenerMethod) {
			block.invoke(viewVariable, "addOnPageChangeListener").arg(_new(onPageChangeListenerClass));
		} else {
			block.invoke(viewVariable, "setOnPageChangeListener").arg(_new(onPageChangeListenerClass));
		}
		return new PageChangeHolder(this, viewVariable, onPageChangeListenerClass);
	}

	@Override
	public JSwitch getOnKeyDownSwitchBody() {
		return keyEventCallbackMethodsDelegate.getOnKeyDownSwitchBody();
	}

	@Override
	public JVar getOnKeyDownKeyEventParam() {
		return keyEventCallbackMethodsDelegate.getOnKeyDownKeyEventParam();
	}

	@Override
	public JSwitch getOnKeyLongPressSwitchBody() {
		return keyEventCallbackMethodsDelegate.getOnKeyLongPressSwitchBody();
	}

	@Override
	public JVar getOnKeyLongPressKeyEventParam() {
		return keyEventCallbackMethodsDelegate.getOnKeyLongPressKeyEventParam();
	}

	@Override
	public JSwitch getOnKeyMultipleSwitchBody() {
		return keyEventCallbackMethodsDelegate.getOnKeyMultipleSwitchBody();
	}

	@Override
	public JVar getOnKeyMultipleKeyEventParam() {
		return keyEventCallbackMethodsDelegate.getOnKeyMultipleKeyEventParam();
	}

	@Override
	public JVar getOnKeyMultipleCountParam() {
		return keyEventCallbackMethodsDelegate.getOnKeyMultipleCountParam();
	}

	@Override
	public JSwitch getOnKeyUpSwitchBody() {
		return keyEventCallbackMethodsDelegate.getOnKeyUpSwitchBody();
	}

	@Override
	public JVar getOnKeyUpKeyEventParam() {
		return keyEventCallbackMethodsDelegate.getOnKeyUpKeyEventParam();
	}

	public JFieldVar getDataBindingField() {
		return dataBindingDelegate.getDataBindingField();
	}

	public IJExpression getDataBindingInflationExpression(IJExpression contentViewId, IJExpression container, boolean attachToRoot) {
		return dataBindingDelegate.getDataBindingInflationExpression(contentViewId, container, attachToRoot);
	}

}
