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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.Extra;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.internal.core.helper.IntentBuilder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class ExtraHandler extends BaseAnnotationHandler<EActivityHolder> implements MethodInjectionHandler<EActivityHolder>, MethodInjectionHandler.AfterAllParametersInjectedHandler<EActivityHolder> {

	private final InjectHelper<EActivityHolder> injectHelper;

	public ExtraHandler(AndroidAnnotationsEnvironment environment) {
		super(Extra.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		/*
		 * TODO since we override setIntent(), we should check that the setIntent()
		 * method can be overridden
		 */

		injectHelper.validate(Extra.class, element, validation);
		if (!validation.isValid()) {
			return;
		}

		validatorHelper.isNotPrivate(element, validation);

		Element param = injectHelper.getParam(element);
		validatorHelper.canBePutInABundle(param, validation);
	}

	@Override
	public void process(Element element, EActivityHolder holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EActivityHolder holder) {
		return holder.getInjectExtrasBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EActivityHolder holder, Element element, Element param) {
		String fieldName = element.getSimpleName().toString();
		String extraKey = extractExtraKey(element, fieldName);

		TypeMirror actualType = codeModelHelper.getActualTypeOfEnclosingElementOfInjectedElement(holder, param);
		BundleHelper bundleHelper = new BundleHelper(getEnvironment(), actualType);

		JFieldVar extraKeyStaticField = getOrCreateStaticExtraField(holder, extraKey, fieldName);
		if (element.getKind() != ElementKind.PARAMETER) {
			holder.getIntentBuilder().getPutExtraMethod(element, new IntentBuilder.IntentExtra(param.asType(), fieldName, extraKeyStaticField));
		}

		AbstractJClass elementClass = codeModelHelper.typeMirrorToJClass(param.asType());

		JMethod injectExtrasMethod = holder.getInjectExtrasMethod();
		JVar extras = holder.getInjectExtras();

		IJExpression restoreMethodCall = bundleHelper.getExpressionToRestoreFromBundle(elementClass, extras, extraKeyStaticField, injectExtrasMethod);

		JBlock ifContainsKey = targetBlock._if(JExpr.invoke(extras, "containsKey").arg(extraKeyStaticField))._then();
		ifContainsKey.assign(fieldRef, restoreMethodCall);
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEActivity(element, valid);
	}

	@Override
	public void afterAllParametersInjected(EActivityHolder holder, ExecutableElement method, List<InjectHelper.ParamHelper> parameterList) {
		List<IntentBuilder.IntentExtra> params = new ArrayList<>();
		for (InjectHelper.ParamHelper paramHelper : parameterList) {
			Element param = paramHelper.getParameterElement();
			String fieldName = param.getSimpleName().toString();
			String extraKey = extractExtraKey(param, fieldName);
			JFieldVar extraKeyStaticField = getOrCreateStaticExtraField(holder, extraKey, fieldName);
			params.add(new IntentBuilder.IntentExtra(param.asType(), fieldName, extraKeyStaticField));
		}
		holder.getIntentBuilder().getPutExtraMethod(method, params);
	}

	private String extractExtraKey(Element element, String fieldName) {
		Extra annotation = element.getAnnotation(Extra.class);
		String extraKey = annotation.value();
		if (extraKey.isEmpty()) {
			extraKey = fieldName;
		}
		return extraKey;
	}

	private JFieldVar getOrCreateStaticExtraField(EActivityHolder holder, String extraKey, String fieldName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(null, fieldName, "Extra");
		JFieldVar staticExtraField = holder.getGeneratedClass().fields().get(staticFieldName);
		if (staticExtraField == null) {
			staticExtraField = holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, getClasses().STRING, staticFieldName, lit(extraKey));
		}
		return staticExtraField;
	}
}
