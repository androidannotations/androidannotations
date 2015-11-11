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
package org.androidannotations.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;

import org.androidannotations.Option;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.holder.BaseGeneratedClassHolder;
import org.androidannotations.internal.helper.AndroidManifestFinder;
import org.androidannotations.internal.rclass.ProjectRClassFinder;
import org.androidannotations.logger.LoggerContext;
import org.androidannotations.logger.appender.FileAppender;

public class Options {

	private final Map<String, Option> supportedOptions = new HashMap<>();
	private final Map<String, String> options;

	public Options(ProcessingEnvironment processingEnvironment) {
		options = processingEnvironment.getOptions();
		addSupportedOption(AndroidManifestFinder.OPTION_MANIFEST);
		addSupportedOption(AndroidManifestFinder.OPTION_LIBRARY);
		addSupportedOption(ProjectRClassFinder.OPTION_RESOURCE_PACKAGE_NAME);
		addSupportedOption(ModelConstants.OPTION_CLASS_SUFFIX);
		addSupportedOption(FileAppender.OPTION_LOG_FILE);
		addSupportedOption(LoggerContext.OPTION_LOG_LEVEL);
		addSupportedOption(LoggerContext.OPTION_LOG_APPENDER_CONSOLE);
		addSupportedOption(LoggerContext.OPTION_LOG_APPENDER_FILE);
		addSupportedOption(BaseGeneratedClassHolder.OPTION_GENERATE_FINAL_CLASSES);
	}

	public void addAllSupportedOptions(List<Option> options) {
		for (Option option : options) {
			addSupportedOption(option);
		}
	}

	private void addSupportedOption(Option option) {
		supportedOptions.put(option.getName(), option);
	}

	public String get(Option option) {
		String value = options.get(option.getName());
		return value != null ? value : option.getDefaultValue();
	}

	public String get(String optionKey) {
		Option option = supportedOptions.get(optionKey);
		if (option != null) {
			return get(option);
		} else {
			return options.get(optionKey);
		}
	}

	public boolean getBoolean(Option option) {
		return Boolean.valueOf(get(option));
	}

	public boolean getBoolean(String optionKey) {
		return Boolean.valueOf(get(optionKey));
	}

	public Set<String> getSupportedOptions() {
		return supportedOptions.keySet();
	}
}
