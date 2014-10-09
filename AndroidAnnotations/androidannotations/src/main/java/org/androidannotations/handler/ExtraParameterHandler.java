/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;

import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

public abstract class ExtraParameterHandler extends BaseAnnotationHandler<GeneratedClassHolder> {

	private Class<? extends Annotation> methodAnnotationClass;
	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();
	private AnnotationHelper annotationHelper;

	public ExtraParameterHandler(Class<? extends Annotation> targetClass, Class<? extends Annotation> methodAnnotationClass, ProcessingEnvironment processingEnvironment) {
		super(targetClass, processingEnvironment);
		this.methodAnnotationClass = methodAnnotationClass;
		this.annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingMethodHasAnnotation(methodAnnotationClass, element, validatedElements, valid);

		validatorHelper.canBePutInABundle(element, valid);
	}

	@Override
	public void process(Element element, GeneratedClassHolder holder) throws Exception {
		// Don't do anything here.
	}

	public JExpression getExtraValue(VariableElement parameter, JVar intent, JVar extras, JBlock block, JMethod annotatedMethod, GeneratedClassHolder holder) {
		String parameterName = parameter.getSimpleName().toString();
		JClass parameterClass = codeModelHelper.typeMirrorToJClass(parameter.asType(), holder);

		String extraKey = getAnnotationValue(parameter);
		if (extraKey == null || extraKey.isEmpty()) {
			extraKey = parameterName;
		}

		BundleHelper bundleHelper = new BundleHelper(annotationHelper, parameter.asType());
		JExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromIntentOrBundle(parameterClass, intent, extras, getStaticExtraField(holder, extraKey), annotatedMethod);

		return block.decl(parameterClass, parameterName, restoreMethodCall);
	}

	private JFieldVar getStaticExtraField(GeneratedClassHolder holder, String extraName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(null, extraName, "Extra");
		JFieldVar staticExtraField = holder.getGeneratedClass().fields().get(staticFieldName);
		if (staticExtraField == null) {
			staticExtraField = holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, holder.classes().STRING, staticFieldName, lit(extraName));
		}
		return staticExtraField;
	}

	public abstract String getAnnotationValue(VariableElement parameter);
}
