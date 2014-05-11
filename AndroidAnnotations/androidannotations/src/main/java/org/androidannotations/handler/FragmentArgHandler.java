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

import com.sun.codemodel.*;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.EFragmentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.*;

public class FragmentArgHandler extends BaseAnnotationHandler<EFragmentHolder> {

	private final AnnotationHelper annotationHelper;
	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public FragmentArgHandler(ProcessingEnvironment processingEnvironment) {
		super(FragmentArg.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEFragment(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);
	}

	@Override
	public void process(Element element, EFragmentHolder holder) {
		FragmentArg annotation = element.getAnnotation(FragmentArg.class);
		String argKey = annotation.value();
		String fieldName = element.getSimpleName().toString();

		if (argKey.isEmpty()) {
			argKey = fieldName;
		}

		BundleHelper bundleHelper = new BundleHelper(annotationHelper, element);
		JFieldVar argKeyStaticField = createStaticArgField(holder, argKey, fieldName);
		injectArgInComponent(element, holder, bundleHelper, argKeyStaticField, fieldName);
		createBuilderInjectionMethod(element, holder, bundleHelper, argKeyStaticField, fieldName);
	}

	private JFieldVar createStaticArgField(EFragmentHolder holder, String argKey, String fieldName) {
		String staticFieldName;
		if (fieldName.endsWith("Arg")) {
			staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(fieldName);
		} else {
			staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(fieldName + "Arg");
		}
		return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, classes().STRING, staticFieldName, lit(argKey));
	}

	private void injectArgInComponent(Element element, EFragmentHolder holder, BundleHelper bundleHelper, JFieldVar extraKeyStaticField, String fieldName) {
		JVar bundle = holder.getInjectBundleArgs();
		JBlock injectExtrasBlock = holder.getInjectArgsBlock();
		JFieldRef extraField = JExpr.ref(fieldName);

		JBlock ifContainsKey = injectExtrasBlock._if(JExpr.invoke(bundle, "containsKey").arg(extraKeyStaticField))._then();
		JExpression restoreMethodCall = JExpr.invoke(bundle, bundleHelper.getMethodNameToRestore()).arg(extraKeyStaticField);
		if (bundleHelper.restoreCallNeedCastStatement()) {

			JClass jclass = codeModelHelper.typeMirrorToJClass(element.asType(), holder);
			restoreMethodCall = JExpr.cast(jclass, restoreMethodCall);

			if (bundleHelper.restoreCallNeedsSuppressWarning()) {
				JMethod injectExtrasMethod = holder.getInjectArgsMethod();
				if (injectExtrasMethod.annotations().size() == 0) {
					injectExtrasMethod.annotate(SuppressWarnings.class).param("value", "unchecked");
				}
			}

		}
		ifContainsKey.assign(extraField, restoreMethodCall);
	}

	private void createBuilderInjectionMethod(Element element, EFragmentHolder holder, BundleHelper bundleHelper, JFieldVar argKeyStaticField, String fieldName) {
		JDefinedClass builderClass = holder.getBuilderClass();
		JFieldRef builderArgsField = holder.getBuilderArgsField();
		TypeMirror elementType = element.asType();
		JClass paramClass = codeModelHelper.typeMirrorToJClass(elementType, holder);

		JMethod method = builderClass.method(PUBLIC, builderClass, fieldName);
		JVar arg = method.param(paramClass, fieldName);
		method.body().invoke(builderArgsField, bundleHelper.getMethodNameToSave()).arg(argKeyStaticField).arg(arg);
		method.body()._return(_this());
	}
}
