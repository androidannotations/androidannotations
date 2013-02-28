/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.processing.EBeanHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

public class GetProcessor extends GetPostProcessor {

	public GetProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Get.class;
	}

	@Override
	public String retrieveUrlSuffix(Element element) {
		Get getAnnotation = element.getAnnotation(Get.class);
		return getAnnotation.value();
	}

	@Override
	protected JExpression generateHttpEntityVar(MethodProcessorHolder methodHolder) {
		ExecutableElement executableElement = (ExecutableElement) methodHolder.getElement();
		EBeanHolder holder = methodHolder.getHolder();
		JClass httpEntity = holder.refClass(CanonicalNameConstants.HTTP_ENTITY);

		JBlock body = methodHolder.getBody();
		JVar httpHeadersVar = generateHttpHeadersVar(holder, body, executableElement);

		boolean hasHeaders = httpHeadersVar != null;

		if (hasHeaders) {
			JInvocation newHttpEntityVarCall = JExpr._new(httpEntity.narrow(Object.class));
			newHttpEntityVarCall.arg(httpHeadersVar);

			String httpEntityVarName = "requestEntity";

			return body.decl(httpEntity.narrow(Object.class), httpEntityVarName, newHttpEntityVarCall);
		} else {
			return JExpr._null();
		}
	}
}
