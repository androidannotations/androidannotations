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
package org.androidannotations.logger;

import java.util.HashMap;
import java.util.Map;

public final class LoggerFactory {

	private static final Map<String, Logger> LOGGER_CACHE = new HashMap<>();

	private LoggerFactory() {
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(simpleLoggerName(clazz));
	}

	private static Logger getLogger(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name argument cannot be null");
		}

		Logger logger = LOGGER_CACHE.get(name);
		if (logger == null) {
			logger = new Logger(LoggerContext.getInstance(), name);
			LOGGER_CACHE.put(name, logger);
		}

		return logger;
	}

	private static String simpleLoggerName(Class<?> clazz) {
		Package classPackage = clazz.getPackage();
		if (classPackage == null) {
			return clazz.getName();
		}

		StringBuilder stringBuilder = new StringBuilder();

		String packageName = classPackage.getName();
		int lastIndex = 0;
		while (true) {
			stringBuilder.append(packageName.charAt(lastIndex)).append(".");

			lastIndex = packageName.indexOf('.', lastIndex);
			if (lastIndex == -1) {
				break;
			}
			lastIndex++;
		}

		stringBuilder.append(clazz.getSimpleName());

		return stringBuilder.toString();
	}
}
