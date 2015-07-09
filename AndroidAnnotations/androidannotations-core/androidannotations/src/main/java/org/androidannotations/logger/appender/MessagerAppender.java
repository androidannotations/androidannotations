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
package org.androidannotations.logger.appender;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import org.androidannotations.logger.Level;
import org.androidannotations.logger.formatter.FormatterSimple;

public class MessagerAppender extends Appender {

	public MessagerAppender() {
		super(new FormatterSimple());
	}

	private Messager messager;

	@Override
	public void open() {
		messager = processingEnv.getMessager();
	}

	@Override
	public void append(Level level, Element element, AnnotationMirror annotationMirror, String message) {
		if (messager == null) {
			return;
		}

		Kind kind = resolveKind(level);
		messager.printMessage(kind, message, element, annotationMirror);
	}

	@Override
	public void close() {
	}

	private Kind resolveKind(Level level) {
		switch (level) {
		case TRACE:
			return Kind.NOTE;
		case DEBUG:
			return Kind.NOTE;
		case INFO:
			return Kind.NOTE;
		case WARN:
			return Kind.WARNING;
		case ERROR:
			return Kind.ERROR;
		}
		return Kind.OTHER;
	}

}
