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
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AndroidValue;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RClass.Res;
import com.googlecode.androidannotations.rclass.RInnerClass;

public class ValueValidator extends ValidatorHelper implements ElementValidator {

	private final RClass rClass;

	private final AndroidValue androidValue;

	public ValueValidator(AndroidValue androidValue, ProcessingEnvironment processingEnv, RClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
		this.androidValue = androidValue;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return androidValue.getTarget();
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validateHasLayout(element, validatedElements, valid);

		TypeMirror fieldTypeMirror = element.asType();

		validateIsAllowedType(element, valid, fieldTypeMirror);

		validateRFieldName(element, valid);

		validateIsNotPrivate(element, valid);

		return valid.isValid();
	}

	private void validateRFieldName(Element element, IsValid valid) {
		int idValue = androidValue.idFromElement(element);

		Res resInnerClass = androidValue.getRInnerClass();

		RInnerClass rInnerClass = rClass.get(resInnerClass);

		if (idValue == AndroidValue.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			if (!rInnerClass.containsField(fieldName)) {
				valid.invalidate();
				printAnnotationError(element, "Id not found: R." + resInnerClass.rName() + "." + fieldName);
			}
		} else {
			if (!rInnerClass.containsIdValue(idValue)) {
				valid.invalidate();
				printAnnotationError(element, "Id with value "+idValue+" not found in R." + resInnerClass.rName() + ".*");
			}
		}
	}

	private void validateIsAllowedType(Element element, IsValid valid, TypeMirror fieldTypeMirror) {

		String qualifiedName = fieldTypeMirror.toString();

		if (!androidValue.getAllowedTypes().contains(qualifiedName)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should only be used on a field which is a " + androidValue.getAllowedTypes().toString()
					+ ", not " + qualifiedName);
		}
	}

	private void validateHasLayout(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(Layout.class);

		if (!layoutAnnotatedElements.contains(enclosingElement)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should only be used on a field in a class annotated with " + annotationName(Layout.class));
		}
	}

}
