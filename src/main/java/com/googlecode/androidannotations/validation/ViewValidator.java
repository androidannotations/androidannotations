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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

public class ViewValidator extends ValidatorHelper implements ElementValidator {

	private static final String ANDROID_VIEW_QUALIFIED_NAME = "android.view.View";
	private final IRClass rClass;
	private final TypeMirror viewTypeMirror;

	public ViewValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
		viewTypeMirror = typeElementFromQualifiedName(ANDROID_VIEW_QUALIFIED_NAME).asType();
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ViewById.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validateEnclosingElementHasLayout(element, validatedElements, valid);

		TypeMirror uiFieldTypeMirror = element.asType();

		validateIsDeclaredType(element, valid, uiFieldTypeMirror);

		validateExtendsViewType(element, valid, uiFieldTypeMirror);

		validateRFieldName(element, valid);

		validateIsNotPrivate(element, valid);

		return valid.isValid();
	}

	private void validateRFieldName(Element element, IsValid valid) {
		ViewById viewAnnotation = element.getAnnotation(ViewById.class);
		int viewIdValue = viewAnnotation.value();

		IRInnerClass rInnerClass = rClass.get(Res.ID);

		if (viewIdValue == ViewById.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			if (!rInnerClass.containsField(fieldName)) {
				valid.invalidate();
				printAnnotationError(element, "Id not found: R.id." + fieldName);
			}
		} else {
			if (!rInnerClass.containsIdValue(viewIdValue)) {
				valid.invalidate();
				printAnnotationError(element, "Id not found: R.id." + viewIdValue);
			}
		}
	}

	private void validateExtendsViewType(Element element, IsValid valid, TypeMirror uiFieldTypeMirror) {
		if (!isSubtype(uiFieldTypeMirror, viewTypeMirror)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should only be used on a field which type extends android.view.View");
		}
	}

	private void validateIsDeclaredType(Element element, IsValid valid, TypeMirror uiFieldTypeMirror) {
		if (!(uiFieldTypeMirror instanceof DeclaredType)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should only be used on a field which is a declared type");
		}
	}

}
