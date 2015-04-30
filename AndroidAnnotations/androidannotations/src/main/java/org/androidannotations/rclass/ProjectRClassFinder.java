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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.Option;
import org.androidannotations.helper.OptionsHelper;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;

public class ProjectRClassFinder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRClassFinder.class);

	private ProcessingEnvironment processingEnv;
	private OptionsHelper optionsHelper;

	public ProjectRClassFinder(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		optionsHelper = new OptionsHelper(processingEnv);
	}

	public Option<IRClass> find(AndroidManifest manifest) {
		Elements elementUtils = processingEnv.getElementUtils();
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
		String resourcePackageName = optionsHelper.getResourcePackageName();
		if (resourcePackageName != null) {
			return resourcePackageName;
		} else {
			return manifest.getApplicationPackage();
		}
	}
}
