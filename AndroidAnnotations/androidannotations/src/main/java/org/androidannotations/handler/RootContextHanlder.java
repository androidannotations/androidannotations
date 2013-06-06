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
package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EBeanHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

public class RootContextHanlder extends BaseAnnotationHandler<EBeanHolder> {

	public RootContextHanlder(ProcessingEnvironment processingEnvironment) {
		super(RootContext.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEBeanAnnotation(element, validatedElements, valid);

		validatorHelper.extendsContext(element, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EBeanHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();

		JBlock body = holder.getInitBody();
		JExpression contextRef = holder.getContextRef();

		if (CanonicalNameConstants.CONTEXT.equals(typeQualifiedName)) {
			body.assign(ref(fieldName), contextRef);
		} else {
			JClass extendingContextClass = holder.refClass(typeQualifiedName);
			body._if(contextRef._instanceof(extendingContextClass)) //
					._then() //
					.assign(ref(fieldName), cast(extendingContextClass, contextRef));
		}
	}
}
