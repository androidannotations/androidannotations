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

import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.HasParameterHandlers;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Part;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.helper.RestSpringClasses;
import org.androidannotations.rest.spring.holder.RestHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;

public class PostHandler extends RestMethodHandler implements HasParameterHandlers<RestHolder> {

	private FieldHandler fieldHandler;
	private PartHandler partHandler;

	public PostHandler(AndroidAnnotationsEnvironment environment) {
		super(Post.class, environment);
		fieldHandler = new FieldHandler(environment);
		partHandler = new PartHandler(environment);
	}

	@Override
	public Iterable<AnnotationHandler> getParameterHandlers() {
		return Arrays.<AnnotationHandler> asList(fieldHandler, partHandler);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		validatorHelper.doesNotReturnPrimitive((ExecutableElement) element, validation);

		restSpringValidatorHelper.urlVariableNamesExistInParametersAndHasOnlyOneEntityParameterOrOneOrMorePostParameter((ExecutableElement) element, validation);

		restSpringValidatorHelper.doesNotMixPartAndFieldAnnotations((ExecutableElement) element, validation);
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Post annotation = element.getAnnotation(Post.class);
		return annotation.value();
	}

	@Override
	protected JExpression getRequestEntity(ExecutableElement element, RestHolder holder, JBlock methodBody, SortedMap<String, JVar> params) {
		JVar httpHeaders = restAnnotationHelper.declareHttpHeaders(element, holder, methodBody);
		JVar entitySentToServer = restAnnotationHelper.getEntitySentToServer(element, params);

		if (entitySentToServer == null) {
			Map<String, String> postParameters = restAnnotationHelper.extractPostParameters(element);

			if (postParameters != null) {
				JClass hashMapClass = getJClass(RestSpringClasses.LINKED_MULTI_VALUE_MAP).narrow(String.class, Object.class);
				entitySentToServer = methodBody.decl(hashMapClass, "postParameters", JExpr._new(hashMapClass));

				for (Map.Entry<String, String> postParameter : postParameters.entrySet()) {
					methodBody.add(entitySentToServer.invoke("add").arg(JExpr.lit(postParameter.getKey())).arg(params.get(postParameter.getValue())));
				}
			}
		}

		return restAnnotationHelper.declareHttpEntity(methodBody, entitySentToServer, httpHeaders);
	}

	private abstract class AbstractPostParamHandler extends BaseAnnotationHandler<GeneratedClassHolder> {

		AbstractPostParamHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
			super(targetClass, environment);
		}

		@Override
		protected void validate(Element element, ElementValidation validation) {
			validatorHelper.enclosingElementHasAnnotation(Post.class, element, validation);

			restSpringValidatorHelper.doesNotHavePathAnnotation(element, validation);

			restSpringValidatorHelper.restInterfaceHasFormConverter(element, validation);
		}

		@Override
		public void process(Element element, GeneratedClassHolder holder) throws Exception {
			// Don't do anything here.
		}
	}

	public class FieldHandler extends AbstractPostParamHandler {

		public FieldHandler(AndroidAnnotationsEnvironment environment) {
			super(Field.class, environment);
		}

		@Override
		protected void validate(Element element, ElementValidation validation) {
			super.validate(element, validation);

			restSpringValidatorHelper.doesNotHavePartAnnotation(element, validation);
		}
	}

	public class PartHandler extends AbstractPostParamHandler {

		public PartHandler(AndroidAnnotationsEnvironment environment) {
			super(Part.class, environment);
		}

		@Override
		protected void validate(Element element, ElementValidation validation) {
			super.validate(element, validation);

			restSpringValidatorHelper.doesNotHaveFieldAnnotation(element, validation);
		}
	}
}
