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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class Logger {

	private final LoggerContext loggerContext;
	private final String name;

	public Logger(LoggerContext loggerContext, String name) {
		this.loggerContext = loggerContext;
		this.name = name;
	}

	public void trace(String message, Object... args) {
		trace(message, null, args);
	}

	public void trace(String message, Element element, Object... args) {
		log(Level.TRACE, message, element, null, null, args);
	}

	public void debug(String message, Object... args) {
		debug(message, null, args);
	}

	public void debug(String message, Element element, Object... args) {
		log(Level.DEBUG, message, element, null, null, args);
	}

	public void info(String message, Object... args) {
		info(message, null, args);
	}

	public void info(String message, Element element, Object... args) {
		log(Level.INFO, message, element, null, null, args);
	}

	public void warn(String message, Object... args) {
		warn(message, null, null, args);
	}

	public void warn(String message, Throwable thr, Object... args) {
		warn(message, null, thr, args);
	}

	public void warn(String message, Element element, Object... args) {
		warn(message, element, null, args);
	}

	public void warn(String message, Element element, Throwable thr, Object... args) {
		log(Level.WARN, message, element, null, thr, args);
	}

	public void error(String message, Object... args) {
		error(message, null, null, args);
	}

	public void error(String message, Element element, Object... args) {
		error(message, element, null, args);
	}

	public void error(String message, Throwable thr, Object... args) {
		error(message, null, thr, args);
	}

	public void error(String message, Element element, Throwable thr, Object... args) {
		log(Level.ERROR, message, element, null, thr, args);
	}

	public boolean isLoggable(Level level) {
		return level.isGreaterOrEquals(loggerContext.getCurrentLevel());
	}

	public void log(Level level, String message, Element element, AnnotationMirror annotationMirror, Throwable thr, Object... args) {
		if (!isLoggable(level)) {
			return;
		}

		loggerContext.writeLog(level, name, message, element, annotationMirror, thr, args);
	}

}
