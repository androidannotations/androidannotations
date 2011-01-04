/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;

public class RunnableValidator extends ValidatorHelper implements ElementValidator {

	private final Class<? extends Annotation> target;

	public RunnableValidator(Class<? extends Annotation> target, ProcessingEnvironment processingEnv) {
		super(processingEnv);
		this.target = target;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return target;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validateHasLayout(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validateHasVoidReturnType(element, executableElement, valid);

		validateIsNotPrivate(element, valid);
		
		validateIsNotFinal(element, valid);

		return valid.isValid();
	}

	private void validateHasVoidReturnType(Element element, ExecutableElement executableElement, IsValid valid) {
		TypeMirror returnType = executableElement.getReturnType();

		if (returnType.getKind() != TypeKind.VOID) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should only be used on a method with a void return type ");
		}
	}

	private void validateHasLayout(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(Layout.class);

		if (!layoutAnnotatedElements.contains(enclosingElement)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should only be used on a method in a class annotated with " + annotationName(Layout.class));
		}
	}
}
