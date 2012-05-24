/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.processing.EBeanHolder;

/**
 * @author Eric Kok
 */
public class SherlockHelper {

	public static final List<String> SHERLOCK_ACTIVITY_CLASS_NAMES = asList( //
			"com.actionbarsherlock.app.SherlockActivity", //
			"com.actionbarsherlock.app.SherlockFragmentActivity", //
			"com.actionbarsherlock.app.SherlockListActivity", //
			"com.actionbarsherlock.app.SherlockExpandableListActivity.java", //
			"com.actionbarsherlock.app.SherlockMapActivity", //
			"com.actionbarsherlock.app.SherlockPreferenceActivity" //
	);

	private final AnnotationHelper annotationHelper;

	private final List<TypeElement> sherlockActivityTypeElements = new ArrayList<TypeElement>();

	public SherlockHelper(AnnotationHelper annotationHelper) {
		this.annotationHelper = annotationHelper;

		for (String sherlockClassName : SHERLOCK_ACTIVITY_CLASS_NAMES) {
			TypeElement sherlockActivityTypeElement = annotationHelper.typeElementFromQualifiedName(sherlockClassName);
			if (sherlockActivityTypeElement != null) {
				sherlockActivityTypeElements.add(sherlockActivityTypeElement);
			}
		}
	}

	/**
	 * Checks whether the Activity extends one of the ActionBarSherlock Activity
	 * types
	 */
	public boolean usesSherlock(EBeanHolder holder) {
		TypeElement annotatedType = annotationHelper.typeElementFromQualifiedName(holder.eBean._extends().fullName());
		for (TypeElement sherlockActivityTypeElement : sherlockActivityTypeElements) {
			if (annotationHelper.isSubtype(annotatedType, sherlockActivityTypeElement)) {
				return true;
			}
		}
		return false;
	}
}
