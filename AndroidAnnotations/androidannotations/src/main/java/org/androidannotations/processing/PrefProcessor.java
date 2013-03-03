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

import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.model.AnnotationElements;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;

public class PrefProcessor implements DecoratingElementProcessor {

	private final AnnotationElements validatedModel;

	public PrefProcessor(AnnotationElements validatedModel) {
		this.validatedModel = validatedModel;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Pref.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		String fieldName = element.getSimpleName().toString();

		TypeMirror fieldTypeMirror = element.asType();

		String fieldType = fieldTypeMirror.toString();
		if (fieldTypeMirror instanceof ErrorType || fieldTypeMirror.getKind() == TypeKind.ERROR) {
			String elementTypeName = fieldTypeMirror.toString();
			String prefTypeName = elementTypeName.substring(0, elementTypeName.length() - GENERATION_SUFFIX.length());
			Set<? extends Element> sharedPrefElements = validatedModel.getRootAnnotatedElements(SharedPref.class.getName());

			for (Element sharedPrefElement : sharedPrefElements) {
				TypeElement sharedPrefTypeElement = (TypeElement) sharedPrefElement;

				String sharedPrefSimpleName = sharedPrefTypeElement.getSimpleName().toString();
				String sharedPrefQualifiedName = sharedPrefTypeElement.getQualifiedName().toString();

				if (sharedPrefSimpleName.equals(prefTypeName)) {
					fieldType = sharedPrefQualifiedName + GENERATION_SUFFIX;
					break;
				}
			}

		}

		JBlock methodBody = holder.initBody;

		JFieldRef field = JExpr.ref(fieldName);

		methodBody.assign(field, JExpr._new(holder.refClass(fieldType)).arg(holder.contextRef));
	}

}
