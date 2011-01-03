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
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.helper.HasTargetAnnotationHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

public class LayoutValidator extends HasTargetAnnotationHelper implements ElementValidator {

	private static final String ANDROID_ACTIVITY_QUALIFIED_NAME = "android.app.Activity";
	private final RClass rClass;
	private final TypeElement activityTypeElement;

	public LayoutValidator(ProcessingEnvironment processingEnv, RClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
		activityTypeElement = typeElementFromQualifiedName(ANDROID_ACTIVITY_QUALIFIED_NAME);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Layout.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		boolean valid = true;

		TypeElement typeElement = (TypeElement) element;
		if (!isSubtype(typeElement, activityTypeElement)) {
			valid = false;
			printAnnotationError(element, annotationName() + " should only be used on Activity subclasses");
		}

		Layout layoutAnnotation = element.getAnnotation(Layout.class);
		int layoutIdValue = layoutAnnotation.value();

		RInnerClass rInnerClass = rClass.get(Res.LAYOUT);

		if (!rInnerClass.containsIdValue(layoutIdValue)) {
			valid = false;
			printAnnotationError(element, "Layout id value not found in R.layout.*: " + layoutIdValue);
		}

		if (isAbstract(element)) {
			valid = false;
			printAnnotationError(element, annotationName() + " should not be used on an abstract class");
		}

		if (isFinal(element)) {
			valid = false;
			printAnnotationError(element, annotationName() + " should not be used on a final class");
		}

		return valid;
	}

}
