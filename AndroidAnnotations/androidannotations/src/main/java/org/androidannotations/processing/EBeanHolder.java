/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.processing;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.lang.model.element.Element;

import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.processing.EBeansHolder.Classes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JVar;

public class EBeanHolder {

	public final JDefinedClass generatedClass;
	/**
	 * Only defined on activities
	 */
	public JVar beforeCreateSavedInstanceStateParam;
	public JBlock initBody;

	public JBlock extrasNotNullBlock;
	public JVar extras;
	public JVar resources;

	public JMethod cast;

	public JFieldVar handler;

	public JBlock onOptionsItemSelectedIfElseBlock;
	public JVar onOptionsItemSelectedItemId;
	public JVar onOptionsItemSelectedItem;

	public JMethod restoreSavedInstanceStateMethod;
	public JBlock saveInstanceStateBlock;

	public JBlock onResumeBlock;
	public JBlock onDestroyBlock;

	public JExpression contextRef;
	/**
	 * Should not be used by inner annotations that target services, broadcast
	 * receivers, and content providers
	 */
	public JBlock initIfActivityBody;
	public JExpression initActivityRef;

	/**
	 * Only defined in activities
	 */
	public JDefinedClass intentBuilderClass;

	/**
	 * Only defined in activities
	 */
	public JFieldVar intentField;

	/**
	 * Only defined in activities
	 */
	public NonConfigurationHolder nonConfigurationHolder;

	/**
	 * TextWatchers by idRef
	 */
	public final HashMap<String, TextWatcherHolder> textWatchers = new HashMap<String, TextWatcherHolder>();

	/**
	 * OnActivityResult byResultCode
	 */
	public final HashMap<Integer, JBlock> onActivityResultCases = new HashMap<Integer, JBlock>();

	public JSwitch onActivityResultSwitch;
	public JMethod onActivityResultMethod;

	/**
	 * onSeekBarChangeListeners by idRef
	 */
	public final HashMap<String, OnSeekBarChangeListenerHolder> onSeekBarChangeListeners = new HashMap<String, OnSeekBarChangeListenerHolder>();

	public JVar fragmentArguments;
	public JFieldVar fragmentArgumentsBuilderField;
	public JMethod fragmentArgumentsInjectMethod;
	public JBlock fragmentArgumentsNotNullBlock;
	public JDefinedClass fragmentBuilderClass;

	public JMethod findNativeFragmentById;
	public JMethod findSupportFragmentById;
	public JMethod findNativeFragmentByTag;
	public JMethod findSupportFragmentByTag;

	public JBlock onCreateOptionMenuMethodBody;
	public JVar onCreateOptionMenuMenuInflaterVariable;
	public JVar onCreateOptionMenuMenuParam;

	private final EBeansHolder eBeansHolder;
	public final Class<? extends Annotation> eBeanAnnotation;

	private ViewChangedHolder viewChangedHolder;

	/**
	 * Only defined in beans that implement {@link HasViews}
	 */
	private JExpression notifier;

	public EBeanHolder(EBeansHolder eBeansHolder, Class<? extends Annotation> eBeanAnnotation, JDefinedClass generatedClass) {
		this.eBeansHolder = eBeansHolder;
		this.eBeanAnnotation = eBeanAnnotation;
		this.generatedClass = generatedClass;
	}

	public Classes classes() {
		return eBeansHolder.classes();
	}

	public JCodeModel codeModel() {
		return eBeansHolder.codeModel();
	}

	public JClass refClass(String fullyQualifiedClassName) {
		return eBeansHolder.refClass(fullyQualifiedClassName);
	}

	public JClass refClass(Class<?> clazz) {
		return eBeansHolder.refClass(clazz);
	}

	public JDefinedClass definedClass(String fullyQualifiedClassName) {
		return eBeansHolder.definedClass(fullyQualifiedClassName);
	}

	public void generateApiClass(Element originatingElement, Class<?> apiClass) {
		eBeansHolder.generateApiClass(originatingElement, apiClass);
	}

	public ViewChangedHolder onViewChanged() {

		if (viewChangedHolder == null) {
			JCodeModel codeModel = eBeansHolder.codeModel();

			generatedClass._implements(OnViewChangedListener.class);
			JMethod onViewChanged = generatedClass.method(PUBLIC, codeModel.VOID, "onViewChanged");
			onViewChanged.annotate(Override.class);
			JVar onViewChangedHasViewsParam = onViewChanged.param(HasViews.class, "hasViews");
			JClass notifierClass = refClass(OnViewChangedNotifier.class);
			initBody.staticInvoke(notifierClass, "registerOnViewChangedListener").arg(_this());

			viewChangedHolder = new ViewChangedHolder(onViewChanged, onViewChangedHasViewsParam);
		}
		return viewChangedHolder;
	}

	public void invokeViewChanged(JBlock block) {
		block.invoke(notifier, "notifyViewChanged").arg(_this());
	}

	public JVar replacePreviousNotifier(JBlock block) {
		JClass notifierClass = refClass(OnViewChangedNotifier.class);
		if (notifier == null) {
			notifier = generatedClass.field(PRIVATE | FINAL, notifierClass, "onViewChangedNotifier_", _new(notifierClass));
			generatedClass._implements(HasViews.class);
		}
		JVar previousNotifier = block.decl(notifierClass, "previousNotifier", notifierClass.staticInvoke("replaceNotifier").arg(notifier));
		return previousNotifier;
	}

	public JVar replacePreviousNotifierWithNull(JBlock block) {
		JClass notifierClass = refClass(OnViewChangedNotifier.class);
		JVar previousNotifier = block.decl(notifierClass, "previousNotifier", notifierClass.staticInvoke("replaceNotifier").arg(_null()));
		return previousNotifier;
	}

	public void resetPreviousNotifier(JBlock block, JVar previousNotifier) {
		JClass notifierClass = refClass(OnViewChangedNotifier.class);
		block.staticInvoke(notifierClass, "replaceNotifier").arg(previousNotifier);
	}

	public void wrapInitWithNotifier() {
		JBlock initBlock = initBody;
		JVar previousNotifier = replacePreviousNotifier(initBlock);
		initBody = initBody.block();
		resetPreviousNotifier(initBlock, previousNotifier);
	}

}
