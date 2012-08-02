/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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

import static com.googlecode.androidannotations.helper.GreenDroidConstants.GREENDROID_ACTIVITIES_LIST_CLASS;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

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
import javax.lang.model.util.ElementFilter;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.api.SdkVersionHelper;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class EActivityProcessor implements ElementProcessor {

	private final IRClass rClass;
	private List<TypeElement> greendroidActivityElements;

	private final AnnotationHelper annotationHelper;

	private final ProcessingEnvironment processingEnv;
	private final APTCodeModelHelper aptCodeModelHelper;

	public EActivityProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		this.processingEnv = processingEnv;
		annotationHelper = new AnnotationHelper(processingEnv);
		aptCodeModelHelper = new APTCodeModelHelper();
		this.rClass = rClass;

		greendroidActivityElements = new ArrayList<TypeElement>();
		for (String greendroidActivityName : GREENDROID_ACTIVITIES_LIST_CLASS) {
			TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(greendroidActivityName);
			if (typeElement != null) {
				greendroidActivityElements.add(typeElement);
			}
		}
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return EActivity.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws Exception {

		EBeanHolder holder = activitiesHolder.create(element, getTarget());

		TypeElement typeElement = (TypeElement) element;

		// Activity
		String annotatedActivityQualifiedName = typeElement.getQualifiedName().toString();

		String subActivityQualifiedName = annotatedActivityQualifiedName + ModelConstants.GENERATION_SUFFIX;

		boolean usesGreenDroid = usesGreenDroid(typeElement);

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

		JClass bundleClass = holder.classes().BUNDLE;

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

		onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);

		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(holder, element, EActivity.class, rClass.get(Res.LAYOUT), false);

		JFieldRef contentViewId;
		if (fieldRefs.size() == 1) {
			contentViewId = fieldRefs.get(0);
		} else {
			contentViewId = null;
		}

		if (contentViewId != null) {
			// GreenDroid support
			if (usesGreenDroid) {
				onCreateBody.invoke("setActionBarContentView").arg(contentViewId);
			} else {
				onCreateBody.invoke("setContentView").arg(contentViewId);
			}
		}

		// Overriding setContentView (with layout id param)
		JClass layoutParamsClass = holder.classes().VIEW_GROUP_LAYOUT_PARAMS;

		String setContentViewMethodName;
		if (usesGreenDroid) {
			setContentViewMethodName = "setActionBarContentView";
		} else {
			setContentViewMethodName = "setContentView";
		}

		setContentViewMethod(setContentViewMethodName, codeModel, holder, new JType[] { codeModel.INT }, new String[] { "layoutResID" });
		setContentViewMethod(setContentViewMethodName, codeModel, holder, new JType[] { holder.classes().VIEW, layoutParamsClass }, new String[] { "view", "params" });
		setContentViewMethod(setContentViewMethodName, codeModel, holder, new JType[] { holder.classes().VIEW }, new String[] { "view" });

		// Handling onBackPressed
		if (hasOnBackPressedMethod(typeElement)) {
			JMethod onKeyDownMethod = holder.eBean.method(PUBLIC, codeModel.BOOLEAN, "onKeyDown");
			onKeyDownMethod.annotate(Override.class);
			JVar keyCodeParam = onKeyDownMethod.param(codeModel.INT, "keyCode");
			JClass keyEventClass = holder.classes().KEY_EVENT;
			JVar eventParam = onKeyDownMethod.param(keyEventClass, "event");

			JClass versionHelperClass = holder.refClass(SdkVersionHelper.class);

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

		if (!isAbstract) {
			aptCodeModelHelper.addActivityIntentBuilder(codeModel, holder);
		}

	}

	private void setContentViewMethod(String setContentViewMethodName, JCodeModel codeModel, EBeanHolder holder, JType[] paramTypes, String[] paramNames) {
		JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, setContentViewMethodName);
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
		List<? extends Element> allMembers = annotationHelper.getElementUtils().getAllMembers(activityElement);

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
				&& parameters.get(0).asType().toString().equals(CanonicalNameConstants.BUNDLE) //
		;
	}

	private boolean hasOnBackPressedMethod(TypeElement activityElement) {

		List<? extends Element> allMembers = annotationHelper.getElementUtils().getAllMembers(activityElement);

		List<ExecutableElement> activityInheritedMethods = ElementFilter.methodsIn(allMembers);

		for (ExecutableElement activityInheritedMethod : activityInheritedMethods) {
			if (isCustomOnBackPressedMethod(activityInheritedMethod)) {
				return true;
			}
		}
		return false;
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

	private boolean usesGreenDroid(TypeElement typeElement) {
		for (TypeElement greendroidActivityElement : greendroidActivityElements) {
			if (processingEnv.getTypeUtils().isSubtype(typeElement.asType(), greendroidActivityElement.asType())) {
				return true;
			}
		}
		return false;
	}
}
