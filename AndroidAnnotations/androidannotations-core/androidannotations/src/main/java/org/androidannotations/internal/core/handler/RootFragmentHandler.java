/**
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

import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.lit;
import static org.androidannotations.helper.LogHelper.logTagForClassHolder;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.RootFragment;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EBeanHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JInvocation;

public class RootFragmentHandler extends BaseAnnotationHandler<EBeanHolder> implements MethodInjectionHandler<EBeanHolder> {

	private final InjectHelper<EBeanHolder> injectHelper;

	public RootFragmentHandler(AndroidAnnotationsEnvironment environment) {
		super(RootFragment.class, environment);
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		injectHelper.validate(RootFragment.class, element, validation);
		if (!validation.isValid()) {
			return;
		}

		Element param = injectHelper.getParam(element);
		validatorHelper.extendsFragment(param, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EBeanHolder holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EBeanHolder holder) {
		return holder.getInitBodyInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EBeanHolder holder, Element element, Element param) {
		TypeMirror elementType = param.asType();
		String typeQualifiedName = elementType.toString();

		IJExpression rootFragmentRef = holder.getRootFragmentRef();

		AbstractJClass extendingContextClass = getEnvironment().getJClass(typeQualifiedName);

		JConditional cond = targetBlock._if(rootFragmentRef._instanceof(extendingContextClass));
		cond._then().add(fieldRef.assign(cast(extendingContextClass, rootFragmentRef)));

		JInvocation warningInvoke = getClasses().LOG.staticInvoke("w");
		warningInvoke.arg(logTagForClassHolder(holder));
		warningInvoke
				.arg(lit("Due to class ").plus(rootFragmentRef.invoke("getClass").invoke("getSimpleName")).plus(lit(", the @RootFragment " + extendingContextClass.name() + " won't be populated")));

		JInvocation warningInvokeIfNull = getClasses().LOG.staticInvoke("w");
		warningInvokeIfNull.arg(logTagForClassHolder(holder));
		warningInvokeIfNull.arg(lit("Due to not having a rootFragment reference the @RootFragment " + extendingContextClass.name() + " won't be populated"));

		JConditional ifNotNull = cond._elseif(rootFragmentRef.ne(_null()));
		ifNotNull._then().add(warningInvoke);
		ifNotNull._else().add(warningInvokeIfNull);
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEBeanAnnotation(element, valid);
	}
}
