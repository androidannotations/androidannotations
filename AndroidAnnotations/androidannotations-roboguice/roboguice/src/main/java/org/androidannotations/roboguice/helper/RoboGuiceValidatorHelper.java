/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.roboguice.helper;

import javax.lang.model.util.Elements;

import org.androidannotations.ElementValidation;
import org.androidannotations.helper.AnnotationHelper;

public class RoboGuiceValidatorHelper {

	private AnnotationHelper annotationHelper;

	public RoboGuiceValidatorHelper(AnnotationHelper annotationHelper) {
		this.annotationHelper = annotationHelper;
	}

	public void hasRoboGuiceJars(ElementValidation valid) {
		Elements elementUtils = annotationHelper.getElementUtils();

		if (elementUtils.getTypeElement(RoboGuiceClasses.ROBO_CONTEXT) == null) {
			valid.addError("Could not find the RoboGuice framework in the classpath, the following class is missing: " + RoboGuiceClasses.ROBO_CONTEXT);
		}

		if (elementUtils.getTypeElement(RoboGuiceClasses.ROBO_APPLICATION) != null) {
			valid.addError("It seems you are using an old version of RoboGuice. Be sure to use version 3.0!");
		}

		if (elementUtils.getTypeElement(RoboGuiceClasses.ON_START_EVENT_OLD) != null) {
			valid.addError("It seems you are using an old version of RoboGuice. Be sure to use version 3.0!");
		}
	}
}
