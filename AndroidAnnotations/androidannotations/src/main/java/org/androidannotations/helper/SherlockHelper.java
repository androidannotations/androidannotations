/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.helper;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.processing.EBeanHolder;

/**
 * @author Eric Kok
 */
public class SherlockHelper {

	private final AnnotationHelper annotationHelper;

	public SherlockHelper(AnnotationHelper annotationHelper) {
		this.annotationHelper = annotationHelper;
	}

	/**
	 * Checks whether the Activity extends one of the ActionBarSherlock Activity
	 * types
	 */
	public boolean usesSherlock(EBeanHolder holder) {
		TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(holder.generatedClass._extends().fullName());

		TypeMirror superType;
		while (!((superType = typeElement.getSuperclass()) instanceof NoType)) {
			typeElement = (TypeElement) ((DeclaredType) superType).asElement();
			if (typeElement.getQualifiedName().toString().startsWith("com.actionbarsherlock.app")) {
				return true;
			}
		}
		return false;
	}
}
