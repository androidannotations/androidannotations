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
package com.googlecode.androidannotations.rclass;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.model.AnnotationElements;

public class RClassFinder extends AnnotationHelper {

	public RClassFinder(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	public IRClass find(AnnotationElements extractedModel) {

		Elements elementUtils = processingEnv.getElementUtils();

		Set<? extends Element> annotatedElements = extractedModel.getAnnotatedElements(Layout.class);

		Iterator<? extends Element> iterator = annotatedElements.iterator();

		if (iterator.hasNext()) {
			TypeElement firstLayoutAnnotatedElement = (TypeElement) iterator.next();

			// TODO better handling at finding R class
			PackageElement firstActivityPackage = elementUtils.getPackageOf(firstLayoutAnnotatedElement);

			TypeElement rType = elementUtils.getTypeElement(firstActivityPackage.getQualifiedName() + "." + "R");

			if (rType != null) {
				return new RClass(rType);
			} else {
				printAnnotationError(firstLayoutAnnotatedElement, Layout.class,
						"In order to find the R class, all Activities annotated with @" + Layout.class.getSimpleName()
								+ " should belong to the same package as the R class, which is not the case for: "
								+ firstLayoutAnnotatedElement);
				return null;
			}
		} else {
			/*
			 * If no element is annotated with Layout, then we do not care about
			 * finding the R class or not.
			 */
			return null;
		}
	}

}
