/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.api.SdkVersionHelper;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class EActivityProcessor extends AnnotationHelper implements ElementProcessor {

	private final IRClass rClass;

	public EActivityProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return EActivity.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws Exception {

		EBeanHolder holder = activitiesHolder.create(element);

		TypeElement typeElement = (TypeElement) element;

		// Activity
		String annotatedActivityQualifiedName = typeElement.getQualifiedName().toString();

		String subActivityQualifiedName = annotatedActivityQualifiedName + ModelConstants.GENERATION_SUFFIX;

		int modifiers;
		boolean isAbstract = element.getModifiers().contains(Modifier.ABSTRACT);
		if (isAbstract) {
			modifiers = JMod.PUBLIC | JMod.ABSTRACT;
		} else {
			modifiers = JMod.PUBLIC | JMod.FINAL;
		}

		holder.eBean = codeModel._class(modifiers, subActivityQualifiedName, ClassType.CLASS);

		JClass annotatedActivity = codeModel.directClass(annotatedActivityQualifiedName);

		holder.eBean._extends(annotatedActivity);

		holder.contextRef = _this();

		// onCreate
		int onCreateVisibility;
		if (isAbstract) {
			onCreateVisibility = inheritedOnCreateVisibility(typeElement);
		} else {
			onCreateVisibility = PUBLIC;
		}
		JMethod onCreate = holder.eBean.method(onCreateVisibility, codeModel.VOID, "onCreate");
		onCreate.annotate(Override.class);

		JClass bundleClass = holder.refClass("android.os.Bundle");

		// beforeSetContentView
		holder.init = holder.eBean.method(PRIVATE, codeModel.VOID, "init_");
		holder.beforeCreateSavedInstanceStateParam = holder.init.param(bundleClass, "savedInstanceState");

		{
			// init if activity
			holder.initIfActivityBody = holder.init.body();
			holder.initActivityRef = _this();
		}

		// afterSetContentView
		holder.afterSetContentView = holder.eBean.method(PRIVATE, codeModel.VOID, "afterSetContentView_");

		JVar onCreateSavedInstanceState = onCreate.param(bundleClass, "savedInstanceState");
		JBlock onCreateBody = onCreate.body();

		onCreateBody.invoke(holder.init).arg(onCreateSavedInstanceState);

		onCreateBody.invoke(JExpr._super(), onCreate).arg(onCreateSavedInstanceState);

		EActivity layoutAnnotation = element.getAnnotation(EActivity.class);
		int layoutIdValue = layoutAnnotation.value();

		JFieldRef contentViewId;
		if (layoutIdValue != Id.DEFAULT_VALUE) {
			IRInnerClass rInnerClass = rClass.get(Res.LAYOUT);
			contentViewId = rInnerClass.getIdStaticRef(layoutIdValue, holder);
		} else {
			contentViewId = null;
		}

		if (contentViewId != null) {
			onCreateBody.invoke("setContentView").arg(contentViewId);
		}

		// Overriding setContentView (with layout id param)
		JClass viewClass = holder.refClass("android.view.View");
		JClass layoutParamsClass = holder.refClass("android.view.ViewGroup.LayoutParams");

		setContentViewMethod(codeModel, holder, new JType[] { codeModel.INT }, new String[] { "layoutResID" });
		setContentViewMethod(codeModel, holder, new JType[] { viewClass, layoutParamsClass }, new String[] { "view", "params" });
		setContentViewMethod(codeModel, holder, new JType[] { viewClass }, new String[] { "view" });

		// Handling onBackPressed
		if (hasOnBackPressedMethod(typeElement)) {
			JMethod onKeyDownMethod = holder.eBean.method(PUBLIC, codeModel.BOOLEAN, "onKeyDown");
			onKeyDownMethod.annotate(Override.class);
			JVar keyCodeParam = onKeyDownMethod.param(codeModel.INT, "keyCode");
			JClass keyEventClass = holder.refClass("android.view.KeyEvent");
			JVar eventParam = onKeyDownMethod.param(keyEventClass, "event");

			JClass versionHelperClass = codeModel.ref(SdkVersionHelper.class);

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

		// SharedPref injection
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		List<VariableElement> activityFields = ElementFilter.fieldsIn(enclosedElements);
		for (VariableElement activityField : activityFields) {
			TypeMirror sharedPreferencesHelperType = processingEnv.getElementUtils().getTypeElement("com.googlecode.androidannotations.api.sharedpreferences.SharedPreferencesHelper").asType();
			if (processingEnv.getTypeUtils().isSubtype(activityField.asType(), sharedPreferencesHelperType)) {

			}
		}

		if (!isAbstract) {
			JClass contextClass = holder.refClass("android.content.Context");
			JClass intentClass = holder.refClass("android.content.Intent");
			
			{
				holder.intentBuilderClass = holder.eBean._class(PUBLIC | STATIC, "IntentBuilder_");
				
				JFieldVar contextField = holder.intentBuilderClass.field(PRIVATE, contextClass, "context_");
				
				holder.intentField = holder.intentBuilderClass.field(PRIVATE | FINAL, intentClass, "intent_");
				{
					// Constructor
					JMethod constructor = holder.intentBuilderClass.constructor(JMod.PUBLIC);
					JVar constructorContextParam = constructor.param(contextClass, "context");
					JBlock constructorBody = constructor.body();
					constructorBody.assign(contextField, constructorContextParam);
					constructorBody.assign(holder.intentField, _new(intentClass).arg(constructorContextParam).arg(holder.eBean.dotclass()));
				}
				
				
				{
					// get()
					JMethod method = holder.intentBuilderClass.method(PUBLIC, intentClass, "get");
					method.body()._return(holder.intentField);
				}
				
				{
					// flags()
					JMethod method = holder.intentBuilderClass.method(PUBLIC, holder.intentBuilderClass, "flags");
					JVar flagsParam = method.param(codeModel.INT, "flags");
					JBlock body = method.body();
					body.invoke(holder.intentField, "setFlags").arg(flagsParam);
					body._return(_this());
				}
				
				{
					// start
					JMethod method = holder.intentBuilderClass.method(PUBLIC, codeModel.VOID, "start");
					method.body().invoke(contextField, "startActivity").arg(holder.intentField);
				}
				
				{
					// intent()
					JMethod method = holder.eBean.method(STATIC | PUBLIC, holder.intentBuilderClass, "intent");
					JVar contextParam = method.param(contextClass, "context");
					method.body()._return(_new(holder.intentBuilderClass).arg(contextParam));
				}
			}
			
		}

	}

	private void setContentViewMethod(JCodeModel codeModel, EBeanHolder holder, JType[] paramTypes, String[] paramNames) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, "setContentView");
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
		body.invoke(holder.afterSetContentView);
	}

	private int inheritedOnCreateVisibility(TypeElement activityElement) {
		List<? extends Element> allMembers = getElementUtils().getAllMembers(activityElement);

		List<ExecutableElement> activityInheritedMethods = ElementFilter.methodsIn(allMembers);

		for (ExecutableElement activityInheritedMethod : activityInheritedMethods) {
			if (isOnCreateMethod(activityInheritedMethod)) {
				Set<Modifier> modifiers = activityInheritedMethod.getModifiers();
				for (Modifier modifier : modifiers) {
					if (modifier == Modifier.PUBLIC) {
						return JMod.PUBLIC;
					} else if (modifier == Modifier.PROTECTED) {
						return JMod.PROTECTED;
					}
				}
				return JMod.PUBLIC;
			}
		}
		return PUBLIC;
	}

	private boolean isOnCreateMethod(ExecutableElement method) {

		List<? extends VariableElement> parameters = method.getParameters();
		return method.getSimpleName().toString().equals("onCreate") //
				&& parameters.size() == 1 //
				&& parameters.get(0).asType().toString().equals("android.os.Bundle") //
		;
	}

	private boolean hasOnBackPressedMethod(TypeElement activityElement) {

		List<? extends Element> allMembers = getElementUtils().getAllMembers(activityElement);

		List<ExecutableElement> activityInheritedMethods = ElementFilter.methodsIn(allMembers);

		for (ExecutableElement activityInheritedMethod : activityInheritedMethods) {
			if (isOnBackPressedMethod(activityInheritedMethod)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOnBackPressedMethod(ExecutableElement method) {
		return method.getSimpleName().toString().equals("onBackPressed") //
				&& method.getThrownTypes().size() == 0 //
				&& method.getModifiers().contains(Modifier.PUBLIC) //
				&& method.getReturnType().getKind().equals(TypeKind.VOID) //
				&& method.getParameters().size() == 0 //
		;
	}

}
