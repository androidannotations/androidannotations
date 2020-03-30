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
package org.androidannotations.helper;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.SourceVersion;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.EProvider;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.EViewGroup;

public abstract class ModelConstants {

	public static final Option OPTION_CLASS_SUFFIX = new Option("classSuffix", "_");

	private static String generationSuffix = "_";
	private static String classSuffix;

	public static final List<Class<? extends Annotation>> VALID_ENHANCED_VIEW_SUPPORT_ANNOTATIONS = asList(EActivity.class, EViewGroup.class, EView.class, EBean.class, EFragment.class);

	public static final List<Class<? extends Annotation>> VALID_ENHANCED_COMPONENT_ANNOTATIONS = asList(EApplication.class, EActivity.class, EViewGroup.class, EView.class, EBean.class, EService.class,
			EIntentService.class, EReceiver.class, EProvider.class, EFragment.class);

	private ModelConstants() {
	}

	public static void init(AndroidAnnotationsEnvironment environment) {
		classSuffix = environment.getOptionValue(OPTION_CLASS_SUFFIX).trim();

		if (classSuffix.isEmpty()) {
			throw new IllegalArgumentException("'" + classSuffix + "' may not be an empty string.");
		}

		if (!SourceVersion.isName("ValidName" + classSuffix) || classSuffix.contains(".")) {
			throw new IllegalArgumentException("'" + classSuffix + "' may not be a valid Java identifier.");
		}
	}

	public static String classSuffix() {
		return classSuffix;
	}

	public static String generationSuffix() {
		return generationSuffix;
	}
}
