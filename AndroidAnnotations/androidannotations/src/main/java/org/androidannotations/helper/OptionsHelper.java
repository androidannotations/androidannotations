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
			return true;
		}
	}

}
