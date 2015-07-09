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
package org.androidannotations.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.annotation.processing.ProcessingEnvironment;

import org.androidannotations.handler.AnnotationHandlers;

public abstract class AndroidAnnotationsPlugin {

	public static List<AndroidAnnotationsPlugin> load() {
		ServiceLoader<AndroidAnnotationsPlugin> serviceLoader = ServiceLoader.load(AndroidAnnotationsPlugin.class, AndroidAnnotationsPlugin.class.getClassLoader());
		List<AndroidAnnotationsPlugin> plugins = new ArrayList<>();
		for (AndroidAnnotationsPlugin plugin : serviceLoader) {
			plugins.add(plugin);
		}
		return plugins;
	}

	@Override
	public String toString() {
		return getName();
	}

	public abstract String getName();
	public abstract void addHandlers(AnnotationHandlers annotationHandlers, ProcessingEnvironment processingEnv);
}
