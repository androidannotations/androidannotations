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
import javax.lang.model.util.Elements;

import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.helper.RoboGuiceConstants;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;

public class RoboGuiceValidator extends ValidatorHelper implements ElementValidator {

	private static final String GUICE_INJECTOR_CLASS = "com.google.inject.Injector";
	private static final String ROBOGUICE_INJECTOR_PROVIDER_CLASS = "roboguice.inject.InjectorProvider";


	public RoboGuiceValidator(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return RoboGuice.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validateHasLayout(element, validatedElements, valid);

		validateHasJars(element, valid);

		return valid.isValid();
	}

	private void validateHasJars(Element element, IsValid valid) {
		Elements elementUtils = processingEnv.getElementUtils();

		if (elementUtils.getTypeElement(ROBOGUICE_INJECTOR_PROVIDER_CLASS) == null) {
			valid.invalidate();
			printAnnotationError(element,
					"Could not find the RoboGuice framework in the classpath, the following class is missing: "
							+ ROBOGUICE_INJECTOR_PROVIDER_CLASS);
		}
		
		if (elementUtils.getTypeElement(RoboGuiceConstants.ROBOGUICE_1_0_APPLICATION_CLASS) == null) {
			
			if (elementUtils.getTypeElement(RoboGuiceConstants.ROBOGUICE_1_1_APPLICATION_CLASS) == null) {
			
				valid.invalidate();
				printAnnotationError(element,
						"Could find neither the GuiceApplication class nor the RoboApplication class in the classpath, are you using RoboGuice 1.0 or 1.1 ?");
			}
		}

		try {
			if (elementUtils.getTypeElement(GUICE_INJECTOR_CLASS) == null) {
				valid.invalidate();
				printAnnotationError(element,
						"Could not find the Guice framework in the classpath, the following class is missing: "
								+ GUICE_INJECTOR_CLASS);
			}
		} catch (RuntimeException e) {
			valid.invalidate();
			printAnnotationError(element,
					"Could not find the Guice framework in the classpath, the following class is missing: "
							+ GUICE_INJECTOR_CLASS);
		}

	}

}
