/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.rclass;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.Option;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;

public class ProjectRClassFinder {

	public static final org.androidannotations.process.Option OPTION_RESOURCE_PACKAGE_NAME = new org.androidannotations.process.Option("resourcePackageName", null);

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRClassFinder.class);

	private AndroidAnnotationsEnvironment environment;

	public ProjectRClassFinder(AndroidAnnotationsEnvironment environment) {
		this.environment = environment;
	}

	public Option<IRClass> find(AndroidManifest manifest) {
		Elements elementUtils = environment.getProcessingEnvironment().getElementUtils();
		String rClass = getRClassPackageName(manifest) + ".R";
		TypeElement rType = elementUtils.getTypeElement(rClass);

		if (rType == null) {
			LOGGER.error("The generated {} class cannot be found", rClass);
			return Option.absent();
		}

		LOGGER.info("Found project R class: {}", rType.toString());

		return Option.<IRClass> of(new RClass(rType));
	}

	public String getRClassPackageName(AndroidManifest manifest) {
		String resourcePackageName = environment.getOptionValue(OPTION_RESOURCE_PACKAGE_NAME);
		if (resourcePackageName != null) {
			return resourcePackageName;
		} else {
			return manifest.getApplicationPackage();
		}
	}
}
