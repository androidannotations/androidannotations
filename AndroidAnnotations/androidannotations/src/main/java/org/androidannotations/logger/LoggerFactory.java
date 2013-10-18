package org.androidannotations.logger;

import java.util.HashMap;
import java.util.Map;

public class LoggerFactory {

	private static final Map<String, Logger> LOGGER_CACHE = new HashMap<String, Logger>();

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(simpleLoggerName(clazz));
	}

	private static Logger getLogger(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name argument cannot be null");
		}

		Logger logger = LOGGER_CACHE.get(name);
		if (logger == null) {
			logger = new Logger(name);
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
