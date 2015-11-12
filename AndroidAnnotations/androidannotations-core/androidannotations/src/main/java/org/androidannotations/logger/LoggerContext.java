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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.logger.appender.Appender;
import org.androidannotations.logger.appender.ConsoleAppender;
import org.androidannotations.logger.appender.FileAppender;
import org.androidannotations.logger.appender.MessagerAppender;
import org.androidannotations.logger.formatter.Formatter;

public final class LoggerContext {

	public static final Option OPTION_LOG_LEVEL = new Option("logLevel", "WARN");
	public static final Option OPTION_LOG_APPENDER_CONSOLE = new Option("logAppenderConsole", "false");
	public static final Option OPTION_LOG_APPENDER_FILE = new Option("logAppenderFile", "true");

	private static LoggerContext instance = null;
	private static final Level DEFAULT_LEVEL = Level.WARN;

	private Level currentLevel = DEFAULT_LEVEL;
	private List<Appender> appenders = new ArrayList<>();

	private LoggerContext() {

	}

	public static LoggerContext getInstance() {
		if (instance == null) {
			synchronized (LoggerContext.class) {
				if (instance == null) {
					instance = new LoggerContext();
				}
			}
		}
		return instance;
	}

	public void writeLog(Level level, String loggerName, String message, Element element, AnnotationMirror annotationMirror, Throwable thr, Object... args) {
		for (Appender appender : appenders) {
			Formatter formatter = appender.getFormatter();
			String log = formatter.buildLog(level, loggerName, message, thr, args);
			appender.append(level, element, annotationMirror, log);
		}
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}

	public void setEnvironment(AndroidAnnotationsEnvironment environment) {
		appenders.clear();
		resolveLogLevel(environment);
		addConsoleAppender(environment);
		addFileAppender(environment);
		appenders.add(new MessagerAppender());

		for (Appender appender : appenders) {
			appender.setEnvironment(environment);
			appender.open();
		}
	}

	public void close() {
		for (Appender appender : appenders) {
			appender.close();
		}
	}

	private void resolveLogLevel(AndroidAnnotationsEnvironment environment) {
		Level level = Level.WARN;
		try {
			level = Level.parse(environment.getOptionValue(OPTION_LOG_LEVEL));
		} catch (Exception ignored) {
			// Do nothing, keep the default value;
		}
		setCurrentLevel(level);
	}


	private void addConsoleAppender(AndroidAnnotationsEnvironment environment) {
		if (environment.getOptionBooleanValue(OPTION_LOG_APPENDER_CONSOLE)) {
			appenders.add(new ConsoleAppender());
		}
	}

	private void addFileAppender(AndroidAnnotationsEnvironment environment) {
		if (environment.getOptionBooleanValue(OPTION_LOG_APPENDER_FILE)) {
			appenders.add(new FileAppender());
		}
	}

}
