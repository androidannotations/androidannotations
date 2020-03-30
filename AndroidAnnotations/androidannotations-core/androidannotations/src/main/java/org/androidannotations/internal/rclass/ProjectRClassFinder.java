/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.internal.rclass;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.internal.exception.RClassNotFoundException;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;
import org.androidannotations.rclass.IRClass;

public class ProjectRClassFinder {

	public static final Option OPTION_RESOURCE_PACKAGE_NAME = new Option("resourcePackageName", null);
	public static final Option OPTION_USE_R2 = new Option("useR2", "false");

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRClassFinder.class);

	private AndroidAnnotationsEnvironment environment;

	public ProjectRClassFinder(AndroidAnnotationsEnvironment environment) {
		this.environment = environment;
	}

	public IRClass find(AndroidManifest manifest) throws RClassNotFoundException {
		Elements elementUtils = environment.getProcessingEnvironment().getElementUtils();
		String rClass = getRClassPackageName(manifest) + "." + getRClassSimpleName();
		TypeElement rType = elementUtils.getTypeElement(rClass);

		if (rType == null) {
			LOGGER.error("The generated {} class cannot be found", rClass);
			throw new RClassNotFoundException("The generated " + rClass + " class cannot be found");
		}

		LOGGER.info("Found project R class: {}", rType.toString());
		return new RClass(rType);
	}

	public String getRClassPackageName(AndroidManifest manifest) {
		String resourcePackageName = environment.getOptionValue(OPTION_RESOURCE_PACKAGE_NAME);
		if (resourcePackageName != null) {
			return resourcePackageName;
		} else {
			return manifest.getApplicationPackage();
		}
	}

	private String getRClassSimpleName() {
		boolean useR2 = environment.getOptionBooleanValue(OPTION_USE_R2);

		return useR2 ? "R2" : "R";
	}
}
