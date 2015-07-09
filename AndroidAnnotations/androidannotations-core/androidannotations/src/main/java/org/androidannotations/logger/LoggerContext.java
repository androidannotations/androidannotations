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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import org.androidannotations.helper.OptionsHelper;
import org.androidannotations.logger.appender.Appender;
import org.androidannotations.logger.appender.ConsoleAppender;
import org.androidannotations.logger.appender.FileAppender;
import org.androidannotations.logger.appender.MessagerAppender;
import org.androidannotations.logger.formatter.Formatter;

public final class LoggerContext {

	private static LoggerContext instance = null;
	private static final Level DEFAULT_LEVEL = Level.DEBUG;

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

	public void setProcessingEnv(ProcessingEnvironment processingEnv) {
		appenders.clear();
		OptionsHelper optionsHelper = new OptionsHelper(processingEnv);
		resolveLogLevel(optionsHelper);
		addConsoleAppender(optionsHelper);
		addFileAppender(optionsHelper);
		appenders.add(new MessagerAppender());

		for (Appender appender : appenders) {
			appender.setProcessingEnv(processingEnv);
			appender.open();
		}
	}

	public void close() {
		for (Appender appender : appenders) {
			appender.close();
		}
	}

	private void resolveLogLevel(OptionsHelper optionsHelper) {
		setCurrentLevel(optionsHelper.getLogLevel());
	}

	private void addConsoleAppender(OptionsHelper optionsHelper) {
		if (optionsHelper.shouldUseConsoleAppender()) {
			appenders.add(new ConsoleAppender());
		}
	}

	private void addFileAppender(OptionsHelper optionsHelper) {
		if (optionsHelper.shouldUseFileAppender()) {
			appenders.add(new FileAppender());
		}
	}

}
