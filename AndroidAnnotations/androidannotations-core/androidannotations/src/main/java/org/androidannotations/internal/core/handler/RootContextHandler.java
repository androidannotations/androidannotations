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

import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EBeanHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JInvocation;

public class RootContextHandler extends BaseAnnotationHandler<EBeanHolder> {

	public RootContextHandler(AndroidAnnotationsEnvironment environment) {
		super(RootContext.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEBeanAnnotation(element, validation);

		validatorHelper.extendsContext(element, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EBeanHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();

		JBlock body = holder.getInitBodyInjectionBlock();
		IJExpression contextRef = holder.getContextRef();

		if (CanonicalNameConstants.CONTEXT.equals(typeQualifiedName)) {
			body.assign(ref(fieldName), contextRef);
		} else {
			AbstractJClass extendingContextClass = getEnvironment().getJClass(typeQualifiedName);
			JConditional cond = body._if(holder.getContextRef()._instanceof(extendingContextClass));
			cond._then() //
					.assign(ref(fieldName), cast(extendingContextClass, holder.getContextRef()));

			JInvocation warningInvoke = getClasses().LOG.staticInvoke("w");
			warningInvoke.arg(holder.getGeneratedClass().name());
			IJExpression expr = lit("Due to Context class ").plus(holder.getContextRef().invoke("getClass").invoke("getSimpleName")).plus(
					lit(", the @RootContext " + extendingContextClass.name() + " won't be populated"));
			warningInvoke.arg(expr);
			cond._else() //
					.add(warningInvoke);
		}
	}
}
