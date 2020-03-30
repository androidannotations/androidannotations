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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.GeneratedClassHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public abstract class ExtraParameterHandler extends BaseAnnotationHandler<GeneratedClassHolder> {

	private Class<? extends Annotation> methodAnnotationClass;

	public ExtraParameterHandler(Class<? extends Annotation> targetClass, Class<? extends Annotation> methodAnnotationClass, AndroidAnnotationsEnvironment environment) {
		super(targetClass, environment);
		this.methodAnnotationClass = methodAnnotationClass;
	}

	@Override
	protected void validate(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasAnnotation(methodAnnotationClass, element, valid);

		validatorHelper.canBePutInABundle(element, valid);
	}

	@Override
	public void process(Element element, GeneratedClassHolder holder) throws Exception {
		// Don't do anything here.
	}

	public IJExpression getExtraValue(VariableElement parameter, JVar extras, JBlock block, JMethod annotatedMethod, GeneratedClassHolder holder) {
		return getExtraValue(parameter, extras, block, annotatedMethod, holder.getGeneratedClass());
	}

	public IJExpression getExtraValue(VariableElement parameter, JVar extras, JBlock block, JMethod annotatedMethod, JDefinedClass generatedClass) {
		String parameterName = parameter.getSimpleName().toString();
		AbstractJClass parameterClass = codeModelHelper.typeMirrorToJClass(parameter.asType());

		String extraKey = getAnnotationValue(parameter);
		if (extraKey == null || extraKey.isEmpty()) {
			extraKey = parameterName;
		}

		BundleHelper bundleHelper = new BundleHelper(getEnvironment(), parameter.asType());
		IJExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromBundle(parameterClass, extras, getStaticExtraField(generatedClass, extraKey), annotatedMethod);

		return block.decl(parameterClass, parameterName, restoreMethodCall);
	}

	private JFieldVar getStaticExtraField(JDefinedClass generatedClass, String extraName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(null, extraName, "Extra");
		JFieldVar staticExtraField = generatedClass.fields().get(staticFieldName);
		if (staticExtraField == null) {
			staticExtraField = generatedClass.field(PUBLIC | STATIC | FINAL, getClasses().STRING, staticFieldName, lit(extraName));
		}
		return staticExtraField;
	}

	public abstract String getAnnotationValue(VariableElement parameter);
}
