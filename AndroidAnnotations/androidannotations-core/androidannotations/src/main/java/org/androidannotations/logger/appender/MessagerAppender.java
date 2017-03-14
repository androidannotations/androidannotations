/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import org.androidannotations.logger.Level;
import org.androidannotations.logger.formatter.FormatterSimple;

public class MessagerAppender extends Appender {

	private static List<Message> errors = new LinkedList<>();

	private Messager messager;

	public MessagerAppender() {
		super(new FormatterSimple());
	}

	@Override
	public void open() {
		messager = processingEnv.getMessager();
	}

	@Override
	public void append(Level level, Element element,
			AnnotationMirror annotationMirror, String message) {
		
		if (messager == null) {
			return;
		}

		Kind kind = resolveKind(level);
		if (!kind.equals(Kind.ERROR)) {
			messager.printMessage(kind, message, element, annotationMirror);
		} else {
			errors.add(new Message(kind, message, element, annotationMirror));
		}
	}

	@Override
	public synchronized void close(boolean lastRound) {
		if (lastRound) {
			for (Message error : errors) {
				messager.printMessage(error.kind, error.message,
						error.element, error.annotationMirror);
			}
			errors.clear();
		}
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

	private class Message {
		Kind kind;
		String message;
		Element element;
		AnnotationMirror annotationMirror;

		Message(Kind kind, String message, Element element,
				AnnotationMirror annotationMirror) {
			super();
			this.kind = kind;
			this.message = message;
			this.element = element;
			this.annotationMirror = annotationMirror;
		}

	}

}
