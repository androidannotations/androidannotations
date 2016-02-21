/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
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
import com.helger.jcodemodel.JVar;

public class FragmentArgHandler extends BaseAnnotationHandler<EFragmentHolder>
		implements MethodInjectionHandler<EFragmentHolder>, MethodInjectionHandler.AfterAllParametersInjectedHandler<EFragmentHolder> {

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
		String fieldName = element.getSimpleName().toString();
		String argKey = extractArgKey(element, fieldName);

		if (element.getKind() != ElementKind.PARAMETER) {
			createBuilderInjectionMethod(holder, element, new ArgHelper(param, argKey));
		}

		TypeMirror actualType = codeModelHelper.getActualTypeOfEnclosingElementOfInjectedElement(holder, param);
		AbstractJClass elementClass = codeModelHelper.typeMirrorToJClass(actualType);
		BundleHelper bundleHelper = new BundleHelper(getEnvironment(), actualType);

		JVar bundle = holder.getInjectBundleArgs();
		JMethod injectExtrasMethod = holder.getInjectArgsMethod();
		JFieldVar extraKeyStaticField = getOrCreateStaticArgField(holder, argKey, fieldName);

		IJExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromBundle(elementClass, bundle, extraKeyStaticField, injectExtrasMethod);

		JConditional conditional = targetBlock._if(JExpr.invoke(bundle, "containsKey").arg(extraKeyStaticField));
		conditional._then().add(fieldRef.assign(restoreMethodCall));
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEFragment(element, valid);
	}

	@Override
	public void afterAllParametersInjected(EFragmentHolder holder, ExecutableElement method, List<InjectHelper.ParamHelper> parameterList) {
		List<ArgHelper> argHelpers = new ArrayList<>();
		for (InjectHelper.ParamHelper paramHelper : parameterList) {
			Element param = paramHelper.getParameterElement();
			String fieldName = param.getSimpleName().toString();
			String argKey = extractArgKey(param, fieldName);
			argHelpers.add(new ArgHelper(param, argKey));
		}
		createBuilderInjectMethod(holder, method, argHelpers);
	}

	private String extractArgKey(Element element, String fieldName) {
		FragmentArg annotation = element.getAnnotation(FragmentArg.class);
		String argKey = annotation.value();
		if (argKey.isEmpty()) {
			argKey = fieldName;
		}
		return argKey;
	}

	private JFieldVar getOrCreateStaticArgField(EFragmentHolder holder, String argKey, String fieldName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(null, fieldName, "Arg");
		JFieldVar staticExtraField = holder.getGeneratedClass().fields().get(staticFieldName);
		if (staticExtraField == null) {
			staticExtraField = holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, getClasses().STRING, staticFieldName, lit(argKey));
		}
		return staticExtraField;
	}

	private void createBuilderInjectionMethod(EFragmentHolder holder, Element element, ArgHelper argHelper) {
		createBuilderInjectMethod(holder, element, Collections.singletonList(argHelper));
	}

	public void createBuilderInjectMethod(EFragmentHolder holder, Element element, List<ArgHelper> argHelpers) {
		JDefinedClass builderClass = holder.getBuilderClass();
		JFieldRef builderArgsField = holder.getBuilderArgsField();

		JMethod builderMethod = builderClass.method(PUBLIC, holder.narrow(builderClass), element.getSimpleName().toString());

		String docComment = getProcessingEnvironment().getElementUtils().getDocComment(element);
		codeModelHelper.addTrimmedDocComment(builderMethod, docComment);

		for (ArgHelper argHelper : argHelpers) {
			String fieldName = argHelper.param.getSimpleName().toString();

			TypeMirror actualType = codeModelHelper.getActualTypeOfEnclosingElementOfInjectedElement(holder, argHelper.param);
			BundleHelper bundleHelper = new BundleHelper(getEnvironment(), actualType);

			JFieldVar argKeyStaticField = getOrCreateStaticArgField(holder, argHelper.argKey, fieldName);

			AbstractJClass paramClass = codeModelHelper.typeMirrorToJClass(actualType);
			JVar arg = builderMethod.param(paramClass, fieldName);
			builderMethod.body().add(bundleHelper.getExpressionToSaveFromField(builderArgsField, argKeyStaticField, arg));

			builderMethod.javadoc().addParam(fieldName).append("value for this Fragment argument");
		}

		builderMethod.javadoc().addReturn().append("the FragmentBuilder to chain calls");
		builderMethod.body()._return(_this());
	}

	private static class ArgHelper {
		private final Element param;
		private final String argKey;

		ArgHelper(Element param, String argKey) {
			this.param = param;
			this.argKey = argKey;
		}
	}
}
