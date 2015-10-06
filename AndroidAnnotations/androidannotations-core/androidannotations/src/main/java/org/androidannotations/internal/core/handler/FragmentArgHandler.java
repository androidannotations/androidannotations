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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.EFragmentHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class FragmentArgHandler extends BaseAnnotationHandler<EFragmentHolder> {

	public FragmentArgHandler(AndroidAnnotationsEnvironment environment) {
		super(FragmentArg.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEFragment(element,  validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.canBePutInABundle(element, validation);
	}

	@Override
	public void process(Element element, EFragmentHolder holder) {
		FragmentArg annotation = element.getAnnotation(FragmentArg.class);
		String argKey = annotation.value();
		String fieldName = element.getSimpleName().toString();

		if (argKey.isEmpty()) {
			argKey = fieldName;
		}

		TypeMirror actualType = codeModelHelper.getActualType(element, holder);

		BundleHelper bundleHelper = new BundleHelper(getEnvironment(), actualType);
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
		return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, getClasses().STRING, staticFieldName, lit(argKey));
	}

	private void injectArgInComponent(Element element, EFragmentHolder holder, BundleHelper bundleHelper, JFieldVar extraKeyStaticField, String fieldName) {
		TypeMirror elementType = codeModelHelper.getActualType(element, holder);
		AbstractJClass elementClass = codeModelHelper.typeMirrorToJClass(elementType);

		JVar bundle = holder.getInjectBundleArgs();
		JBlock injectExtrasBlock = holder.getInjectArgsBlock();
		JMethod injectExtrasMethod = holder.getInjectArgsMethod();
		JFieldRef extraField = JExpr.ref(fieldName);

		JBlock ifContainsKey = injectExtrasBlock._if(JExpr.invoke(bundle, "containsKey").arg(extraKeyStaticField))._then();
		IJExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromBundle(elementClass, bundle, extraKeyStaticField, injectExtrasMethod);
		ifContainsKey.assign(extraField, restoreMethodCall);
	}

	private void createBuilderInjectionMethod(Element element, EFragmentHolder holder, BundleHelper bundleHelper, JFieldVar argKeyStaticField, String fieldName) {
		JDefinedClass builderClass = holder.getBuilderClass();
		JFieldRef builderArgsField = holder.getBuilderArgsField();
		TypeMirror type = codeModelHelper.getActualType(element, holder);
		AbstractJClass paramClass = codeModelHelper.typeMirrorToJClass(type);

		JMethod method = builderClass.method(PUBLIC, holder.narrow(builderClass), fieldName);
		JVar arg = method.param(paramClass, fieldName);
		method.body().add(bundleHelper.getExpressionToSaveFromField(builderArgsField, argKeyStaticField, arg));
		method.body()._return(_this());

		String docComment = getProcessingEnvironment().getElementUtils().getDocComment(element);
		codeModelHelper.addTrimmedDocComment(method, docComment);
		method.javadoc().addParam(fieldName).append("the Fragment argument");
		method.javadoc().addReturn().append("the FragmentBuilder to chain calls");
	}
}
