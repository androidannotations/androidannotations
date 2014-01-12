/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
		TRACE("trace"), //
		ANDROID_MANIFEST_FILE("androidManifestFile"), //
		RESOURCE_PACKAGE_NAME("resourcePackageName"), //
		LOG_FILE("logFile"), //
		LOG_LEVEL("logLevel"), //
		LOG_APPENDER_CONSOLE("logAppenderConsole");

		private String key;

		private Option(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	private final Map<String, String> options;

	public OptionsHelper(ProcessingEnvironment processingEnvironment) {
		options = processingEnvironment.getOptions();
	}

	public static Set<String> getOptionsConstants() {
		TreeSet<String> set = new TreeSet<String>();
		for (Option optionEnum : Option.values()) {
			set.add(optionEnum.getKey());
		}
		return set;
	}

	public boolean shouldLogTrace() {
		return getBoolean(Option.TRACE);
	}

	public String getAndroidManifestFile() {
		return getString(Option.ANDROID_MANIFEST_FILE);
	}

	public String getResourcePackageName() {
		return getString(Option.RESOURCE_PACKAGE_NAME);
	}

	public String getLogFile() {
		return getString(Option.LOG_FILE);
	}

	public Level getLogLevel() {
		try {
			return Level.parse(getString(Option.LOG_LEVEL));
		} catch (Exception e) {
			return Level.DEBUG;
		}
	}

	public boolean shouldUseConsoleAppender() {
		return getBoolean(Option.LOG_APPENDER_CONSOLE);
	}

	private String getString(Option option) {
		return options.get(option.getKey());
	}

	private boolean getBoolean(Option option) {
		String key = option.getKey();
		if (options.containsKey(key)) {
			String trace = options.get(key);
			return !"false".equals(trace);
		} else {
			return false;
		}
	}

}
