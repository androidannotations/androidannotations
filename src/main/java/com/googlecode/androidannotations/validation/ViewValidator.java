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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.View;
import com.googlecode.androidannotations.helper.HasTargetAnnotationHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

public class ViewValidator extends HasTargetAnnotationHelper implements ElementValidator {

	private static final String ANDROID_VIEW_QUALIFIED_NAME = "android.view.View";
	private final RClass rClass;
	private final TypeMirror viewTypeMirror;

	public ViewValidator(ProcessingEnvironment processingEnv, RClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
		viewTypeMirror = typeElementFromQualifiedName(ANDROID_VIEW_QUALIFIED_NAME).asType();
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return View.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		Element enclosingElement = element.getEnclosingElement();

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(Layout.class);

		if (layoutAnnotatedElements.contains(enclosingElement)) {

			TypeMirror uiFieldTypeMirror = element.asType();

			if (uiFieldTypeMirror instanceof DeclaredType) {
				if (isSubtype(uiFieldTypeMirror, viewTypeMirror)) {

					View viewAnnotation = element.getAnnotation(View.class);
					int viewIdValue = viewAnnotation.value();

					RInnerClass rInnerClass = rClass.get(Res.ID);

					if (viewIdValue == View.DEFAULT_VALUE) {
						String fieldName = element.getSimpleName().toString();
						if (rInnerClass.containsField(fieldName)) {
							return true;
						} else {
							printAnnotationError(element, "Id not found: R.id." + fieldName);
						}
					} else {
						if (rInnerClass.containsIdValue(viewIdValue)) {
							return true;
						} else {
							printAnnotationError(element, "Id not found: R.id." + viewIdValue);
						}
					}
				} else {
					printAnnotationError(element, "@" + View.class.getSimpleName()
							+ " should only be used on a field which type extends android.view.View");
				}
			} else {
				printAnnotationError(element, "@" + View.class.getSimpleName()
						+ " should only be used on a field which is a declared type");
			}
		} else {
			printAnnotationError(element, "@" + View.class.getSimpleName()
					+ " should only be used on a field in a class annotated with @" + Layout.class.getSimpleName());
		}
		return false;
	}

}
