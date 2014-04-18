/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.helper;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.holder.EComponentHolder;

public class ActionBarSherlockHelper {

	private final AnnotationHelper annotationHelper;

	public ActionBarSherlockHelper(AnnotationHelper annotationHelper) {
		this.annotationHelper = annotationHelper;
	}

	/**
	 * Checks whether the Activity extends one of the ActionBarSherlock Activity
	 * types
	 */
	public boolean usesActionBarSherlock(EComponentHolder holder) {
		TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(holder.getGeneratedClass()._extends().fullName());
		return usesActionBarSherlock(typeElement);
	}

	/**
	 * Checks whether the Activity extends one of the ActionBarSherlock Activity
	 * types
	 */
	public boolean usesActionBarSherlock(TypeElement typeElement) {
		TypeMirror superType;
		while (!((superType = typeElement.getSuperclass()) instanceof NoType)) {
			typeElement = (TypeElement) ((DeclaredType) superType).asElement();
			String qName = typeElement.getQualifiedName().toString();
			if (qName.startsWith("com.actionbarsherlock.app")) {
				return true;
			}
			if (qName.startsWith("org.holoeverywhere")) {
				// Since 2.0.0, HoloEverywhere no longer depends on ABS so we
				// had to find a way to to detect the version in classpath.
				// Since org.holoeverywhere.addon.AddonSherlock has been removed
				// during ABS to ABC migration, we're checking if this class is
				// in the classpath. If so, we're using HEW < 2.0
				// See issue #776
				return annotationHelper.typeElementFromQualifiedName("org.holoeverywhere.addon.AddonSherlock") != null;
			}
		}
		return false;
	}
}
