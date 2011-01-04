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
import com.googlecode.androidannotations.annotations.Value;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.helper.HasTargetAnnotationHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RClass.Res;
import com.googlecode.androidannotations.rclass.RInnerClass;

public class ValueValidator extends HasTargetAnnotationHelper implements ElementValidator {

	private static final String INTEGER_TYPE = "java.lang.Integer";
	private static final String INT_TYPE = "int";
	private static final String STRING_TYPE = "java.lang.String";
	private static final String STRING_ARRAY_TYPE = "java.lang.String[]";
	private final RClass rClass;

	public ValueValidator(ProcessingEnvironment processingEnv, RClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Value.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validateHasLayout(element, validatedElements, valid);

		TypeMirror fieldTypeMirror = element.asType();

		validateIsStringOrStringArrayOrInt(element, valid, fieldTypeMirror);

		validateRFieldName(element, valid, fieldTypeMirror);

		validateIsNotPrivate(element, valid);

		return valid.isValid();
	}

	private void validateIsNotPrivate(Element element, IsValid valid) {
		if (isPrivate(element)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should not be used on a private field");
		}
	}

	private void validateRFieldName(Element element, IsValid valid, TypeMirror fieldTypeMirror) {
		Value viewAnnotation = element.getAnnotation(Value.class);
		int viewIdValue = viewAnnotation.value();

		String qualifiedName = fieldTypeMirror.toString();
		Res resInnerClass;
		if (qualifiedName.equals(STRING_TYPE)) {
			resInnerClass = Res.STRING;
		} else if (qualifiedName.equals(INT_TYPE) || qualifiedName.equals(INTEGER_TYPE)) {
			resInnerClass = Res.COLOR;
		} else if (qualifiedName.equals(STRING_ARRAY_TYPE)) {
			resInnerClass = Res.ARRAY;
		} else {
			// Should already have been marked invalidate by previous validation
			valid.invalidate();
			return;
		}

		RInnerClass rInnerClass = rClass.get(resInnerClass);

		if (viewIdValue == ViewById.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			if (!rInnerClass.containsField(fieldName)) {
				valid.invalidate();
				printAnnotationError(element, "Id not found: R." + resInnerClass.rName() + "." + fieldName);
			}
		} else {
			if (!rInnerClass.containsIdValue(viewIdValue)) {
				valid.invalidate();
				printAnnotationError(element, "Id not found: R." + resInnerClass.rName() + "." + viewIdValue);
			}
		}
	}

	private void validateIsStringOrStringArrayOrInt(Element element, IsValid valid, TypeMirror fieldTypeMirror) {

		String qualifiedName = fieldTypeMirror.toString();

		if (!(qualifiedName.equals(STRING_TYPE) || qualifiedName.equals(STRING_ARRAY_TYPE) || qualifiedName.equals(INT_TYPE) || qualifiedName.equals(INTEGER_TYPE))) {
			valid.invalidate();
			printAnnotationError(element, annotationName()
					+ " should only be used on a field which is a String, a String array, an int or an Integer, not "
					+ qualifiedName);
		}
	}

	private void validateHasLayout(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(Layout.class);

		if (!layoutAnnotatedElements.contains(enclosingElement)) {
			valid.invalidate();
			printAnnotationError(element, annotationName()
					+ " should only be used on a field in a class annotated with " + annotationName(Layout.class));
		}
	}

}
