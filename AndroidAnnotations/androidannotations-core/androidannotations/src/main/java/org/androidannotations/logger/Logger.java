/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
		log(Level.TRACE, message, null, null, null, args);
	}

	public void debug(String message, Object... args) {
		log(Level.DEBUG, message, null, null, null, args);
	}

	public void info(String message, Object... args) {
		log(Level.INFO, message, null, null, null, args);
	}

	public void warn(String message, Object... args) {
		warn(null, message, args);
	}

	public void warn(Element element, String message, Object... args) {
		log(Level.WARN, message, element, null, null, args);
	}

	public void warn(Element element, AnnotationMirror annotationMirror, String message) {
		log(Level.WARN, message, element, annotationMirror, null);
	}

	public void error(String message, Object... args) {
		error(null, null, message, args);
	}

	public void error(Element element, String message, Object... args) {
		error(element, null, message, args);
	}

	public void error(Throwable thr, String message, Object... args) {
		error(null, thr, message, args);
	}

	public void error(Element element, Throwable thr, String message, Object... args) {
		log(Level.ERROR, message, element, null, thr, args);
	}

	public void error(Element element, AnnotationMirror annotationMirror, String message) {
		log(Level.ERROR, message, element, annotationMirror, null);
	}

	public boolean isLoggable(Level level) {
		return level.isGreaterOrEquals(loggerContext.getCurrentLevel());
	}

	private void log(Level level, String message, Element element, AnnotationMirror annotationMirror, Throwable thr, Object... args) {
		if (!isLoggable(level)) {
			return;
		}

		loggerContext.writeLog(level, name, message, element, annotationMirror, thr, args);
	}

}
