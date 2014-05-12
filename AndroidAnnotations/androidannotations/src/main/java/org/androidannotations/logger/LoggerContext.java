/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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

import org.androidannotations.helper.OptionsHelper;
import org.androidannotations.logger.appender.Appender;
import org.androidannotations.logger.appender.ConsoleAppender;
import org.androidannotations.logger.appender.FileAppender;
import org.androidannotations.logger.appender.MessagerAppender;
import org.androidannotations.logger.formatter.Formatter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

public class LoggerContext {

	private static LoggerContext INSTANCE = null;
	private static final Level DEFAULT_LEVEL = Level.DEBUG;

	private Level currentLevel = DEFAULT_LEVEL;
	private List<Appender> appenders = new ArrayList<Appender>();

	public static LoggerContext getInstance() {
		if (INSTANCE == null) {
			synchronized (LoggerContext.class) {
				if (INSTANCE == null) {
					INSTANCE = new LoggerContext();
				}
			}
		}
		return INSTANCE;
	}

	LoggerContext() {
		appenders.add(new FileAppender());
		appenders.add(new MessagerAppender());
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
		OptionsHelper optionsHelper = new OptionsHelper(processingEnv);
		resolveLogLevel(optionsHelper);
		addConsoleAppender(optionsHelper);

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

}
