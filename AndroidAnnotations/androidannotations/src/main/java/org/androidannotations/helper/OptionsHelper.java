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
package org.androidannotations.helper;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;

import org.androidannotations.logger.Level;

public class OptionsHelper {

	enum Option {
		TRACE("trace", "false"), //
		THREAD_CONTROL("threadControl", "true"), //
		ANDROID_MANIFEST_FILE("androidManifestFile", null), //
		RESOURCE_PACKAGE_NAME("resourcePackageName", null), //
		LOG_FILE("logFile", null), //
		LOG_LEVEL("logLevel", "DEBUG"), //
		LOG_APPENDER_CONSOLE("logAppenderConsole", "false"), //
		LOG_APPENDER_FILE("logAppenderFile", "true"), //
		CLASS_SUFFIX("classSuffix", "_");

		private String key;
		private String defaultValue;

		private Option(String key, String defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
		}

		public String getKey() {
			return key;
		}

		public String getDefaultValue() {
			return defaultValue;
		}
	}

	private final Map<String, String> options;

	public OptionsHelper(ProcessingEnvironment processingEnvironment) {
		options = processingEnvironment.getOptions();
	}

	public static Set<String> getOptionsConstants() {
		Set<String> set = new TreeSet<>();
		for (Option optionEnum : Option.values()) {
			set.add(optionEnum.getKey());
		}
		return set;
	}

	public boolean shouldLogTrace() {
		return getBoolean(Option.TRACE);
	}

	public boolean shouldEnsureThreadControl() {
		return getBoolean(Option.THREAD_CONTROL);
	}

	public String getAndroidManifestFile() {
		return getString(Option.ANDROID_MANIFEST_FILE);
	}

	public String getResourcePackageName() {
		return getString(Option.RESOURCE_PACKAGE_NAME);
	}

	public String getClassSuffix() {
		return getString(Option.CLASS_SUFFIX);
	}

	public String getLogFile() {
		return getString(Option.LOG_FILE);
	}

	public Level getLogLevel() {
		try {
			return Level.parse(getString(Option.LOG_LEVEL));
		} catch (Exception e) {
			return Level.parse(Option.LOG_LEVEL.getDefaultValue());
		}
	}

	public boolean shouldUseConsoleAppender() {
		return getBoolean(Option.LOG_APPENDER_CONSOLE);
	}

	public boolean shouldUseFileAppender() {
		return getBoolean(Option.LOG_APPENDER_FILE);
	}

	private String getString(Option option) {
		String key = option.getKey();
		if (options.containsKey(key)) {
			return options.get(key);
		} else {
			return option.getDefaultValue();
		}
	}

	private boolean getBoolean(Option option) {
		return Boolean.valueOf(getString(option));
	}

}
