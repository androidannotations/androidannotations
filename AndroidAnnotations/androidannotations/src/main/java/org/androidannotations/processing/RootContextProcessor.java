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
package org.androidannotations.processing;

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;
import static org.androidannotations.helper.CanonicalNameConstants.CONTEXT;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.RootContext;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class RootContextProcessor implements DecoratingElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return RootContext.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();

		String typeQualifiedName = elementType.toString();

		JBlock body = holder.initBody;
		if (CONTEXT.equals(typeQualifiedName)) {
			body.assign(ref(fieldName), holder.contextRef);
		} else {
			JClass extendingContextClass = holder.refClass(typeQualifiedName);
			body._if(holder.contextRef._instanceof(extendingContextClass)) //
					._then() //
					.assign(ref(fieldName), cast(extendingContextClass, holder.contextRef));
		}
	}

}
