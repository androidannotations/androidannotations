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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import org.androidannotations.logger.Level;
import org.androidannotations.logger.formatter.FormatterFull;

public class ConsoleAppender extends Appender {

	public ConsoleAppender() {
		super(new FormatterFull());
	}

	@Override
	public void open() {
	}

	@Override
	public void append(Level level, Element element, AnnotationMirror annotationMirror, String message) {
		if (level.isSmaller(Level.ERROR)) {
			System.out.println(message);
		} else {
			System.err.println(message);
		}
	}

	@Override
	public void close() {
	}

}
