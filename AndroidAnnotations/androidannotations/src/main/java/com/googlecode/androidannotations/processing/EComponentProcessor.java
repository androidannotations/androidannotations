/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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

import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.EComponent;
import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class EComponentProcessor extends AnnotationHelper implements
		ElementProcessor {

	private final IRClass rClass;

	public EComponentProcessor(ProcessingEnvironment processingEnv,
			IRClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return EComponent.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel,
			ActivitiesHolder activitiesHolder) throws Exception {

		ActivityHolder holder = activitiesHolder.create(element);

		TypeElement typeElement = (TypeElement) element;
		
		String annotatedActivityQualifiedName = typeElement.getQualifiedName()
				.toString();

		String subActivityQualifiedName = annotatedActivityQualifiedName
				+ ModelConstants.GENERATION_SUFFIX;

		int modifiers;
		if (element.getModifiers().contains(Modifier.ABSTRACT)) {
			modifiers = JMod.PUBLIC | JMod.ABSTRACT;
		} else {
			modifiers = JMod.PUBLIC | JMod.FINAL;
		}

		holder.activity = codeModel._class(modifiers, subActivityQualifiedName,
				ClassType.CLASS);

		JClass annotatedActivity = codeModel
				.directClass(annotatedActivityQualifiedName);

		holder.activity._extends(annotatedActivity);

		holder.bundleClass = holder.refClass("android.os.Bundle");

		// afterSetContentView
		holder.afterSetContentView = holder.activity.method(PRIVATE,
				codeModel.VOID, "afterSetContentView_");

		// setcontentview
		JMethod setContentView = holder.activity.method(PRIVATE,
				codeModel.VOID, "setContentView_");
		JVar context = setContentView.param(
				holder.refClass("android.content.Context"), "context");

		// inflate layout if ID is given on annotation
		EComponent layoutAnnotation = element.getAnnotation(EComponent.class);
		int layoutIdValue = layoutAnnotation.value();
		JFieldRef contentViewId;
		if (layoutIdValue != Id.DEFAULT_VALUE) {
			IRInnerClass rInnerClass = rClass.get(Res.LAYOUT);
			contentViewId = rInnerClass.getIdStaticRef(layoutIdValue, holder);
			JClass layoutInflaterClass = holder
					.refClass("android.view.LayoutInflater");
			
			JVar inflater = setContentView.body().decl( //
					layoutInflaterClass,
					"inflater_",
					layoutInflaterClass.staticInvoke("from").arg(context));
			
			setContentView.body().invoke(inflater, "inflate")
					.arg(contentViewId).arg(JExpr._this());
		}

		// finally
		setContentView.body().invoke(holder.afterSetContentView);
		
		copyConstructorsWithCallToAfterSetContentView(element, holder, setContentView);

	}

	private void copyConstructorsWithCallToAfterSetContentView(Element element, ActivityHolder holder, JMethod setContentViewMethod) {
		List<ExecutableElement> constructors = new ArrayList<ExecutableElement>();
		for (Element e : element.getEnclosedElements()) {
			if (e.getKind() == CONSTRUCTOR) {
				constructors.add((ExecutableElement) e);
			}
		}

		for (ExecutableElement userConstructor : constructors) {
			JMethod copy = holder.activity.constructor(PUBLIC);
			JInvocation superCall = copy.body().invoke("super");
			String contextParamName = null;
			for (VariableElement param : userConstructor.getParameters()) {
				String paramName = param.getSimpleName().toString();
				String paramType = param.asType().toString();
				if (paramType.equals("android.content.Context")) {
					contextParamName = paramName;
				}
				copy.param(holder.refClass(paramType), paramName);
				superCall.arg(JExpr.ref(paramName));
			}
			copy.body().invoke(setContentViewMethod).arg(JExpr.ref(contextParamName));
		}
	}

}
