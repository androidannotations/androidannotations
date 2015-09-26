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
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EFragmentHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.JVar;

public class FragmentArgHandler extends BaseAnnotationHandler<EFragmentHolder>implements MethodInjectionHandler<EFragmentHolder> {

	private final InjectHelper<EFragmentHolder> injectHelper;

	public FragmentArgHandler(AndroidAnnotationsEnvironment environment) {
		super(FragmentArg.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		injectHelper.validate(FragmentArg.class, element, validation);

		validatorHelper.isNotPrivate(element, validation);

		Element param = injectHelper.getParam(element);
		validatorHelper.canBePutInABundle(param, validation);
	}

	@Override
	public void process(Element element, EFragmentHolder holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EFragmentHolder holder) {
		return holder.getInjectArgsBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EFragmentHolder holder, Element element, Element param) {
		FragmentArg annotation = element.getAnnotation(FragmentArg.class);
		String argKey = annotation.value();
		String fieldName = element.getSimpleName().toString();

		if (argKey.isEmpty()) {
			argKey = fieldName;
		}

		DeclaredType enclosingClassType;
		if (param.getKind() == ElementKind.PARAMETER) {
			enclosingClassType = (DeclaredType) param.getEnclosingElement().getEnclosingElement().asType();
		} else {
			enclosingClassType = (DeclaredType) param.getEnclosingElement().asType();
		}
		TypeMirror actualType = codeModelHelper.getActualType(param, enclosingClassType, holder);

		BundleHelper bundleHelper = new BundleHelper(getEnvironment(), actualType);
		JFieldVar extraKeyStaticField = createStaticArgField(holder, argKey, fieldName);
		createBuilderInjectionMethod(param, actualType, holder, bundleHelper, extraKeyStaticField, fieldName);

		AbstractJClass elementClass = codeModelHelper.typeMirrorToJClass(actualType);

		JVar bundle = holder.getInjectBundleArgs();
		JMethod injectExtrasMethod = holder.getInjectArgsMethod();

		IJExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromBundle(elementClass, bundle, extraKeyStaticField, injectExtrasMethod);

		JConditional conditional = targetBlock._if(JExpr.invoke(bundle, "containsKey").arg(extraKeyStaticField));
		conditional._then().add(fieldRef.assign(restoreMethodCall));
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEFragment(element, valid);
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

	private void createBuilderInjectionMethod(Element element, TypeMirror actualType, EFragmentHolder holder, BundleHelper bundleHelper, JFieldVar argKeyStaticField, String fieldName) {
		JDefinedClass builderClass = holder.getBuilderClass();
		JFieldRef builderArgsField = holder.getBuilderArgsField();
		AbstractJClass paramClass = codeModelHelper.typeMirrorToJClass(actualType);

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
