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
package org.androidannotations.rest.spring.handler;

import java.util.Map;
import java.util.SortedMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.rest.spring.helper.RestSpringClasses;
import org.androidannotations.rest.spring.holder.RestHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JVar;

public abstract class AbstractRestMethodWithParameterHandler extends RestMethodHandler {

	public AbstractRestMethodWithParameterHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		super(targetClass, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		validatorHelper.doesNotReturnPrimitive((ExecutableElement) element, validation);

		restSpringValidatorHelper.doesNotHaveDuplicateFieldAndPartName((ExecutableElement) element, validation);
		restSpringValidatorHelper.hasOneOrZeroBodyParameter((ExecutableElement) element, validation);
		restSpringValidatorHelper.doesNotMixRequestEntityAnnotations((ExecutableElement) element, validation);
	}

	@Override
	protected IJExpression getRequestEntity(ExecutableElement element, RestHolder holder, JBlock methodBody, SortedMap<String, JVar> params) {
		JVar httpHeaders = restAnnotationHelper.declareHttpHeaders(element, holder, methodBody);
		JVar entitySentToServer = restAnnotationHelper.getEntitySentToServer(element, params);

		if (entitySentToServer == null) {
			Map<String, String> parameters = restAnnotationHelper.extractFieldAndPartParameters(element);

			if (parameters != null) {
				AbstractJClass hashMapClass = getJClass(RestSpringClasses.LINKED_MULTI_VALUE_MAP).narrow(String.class, Object.class);
				entitySentToServer = methodBody.decl(hashMapClass, "parameters", JExpr._new(hashMapClass));

				for (Map.Entry<String, String> parameter : parameters.entrySet()) {
					methodBody.add(entitySentToServer.invoke("add").arg(JExpr.lit(parameter.getKey())).arg(params.get(parameter.getValue())));
				}
			}
		}

		return restAnnotationHelper.declareHttpEntity(methodBody, entitySentToServer, httpHeaders);
	}
}
