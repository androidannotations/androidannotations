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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import org.androidannotations.helper.OptionsHelper;
import org.androidannotations.logger.Level;
import org.androidannotations.logger.formatter.Formatter;

public abstract class Appender {

	protected final Formatter formatter;
	protected ProcessingEnvironment processingEnv;
	protected OptionsHelper optionsHelper;

	public Appender(Formatter formatter) {
		this.formatter = formatter;
	}

	public Formatter getFormatter() {
		return formatter;
	}

	public void setProcessingEnv(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		optionsHelper = new OptionsHelper(processingEnv);
	}

	public abstract void open();

	public abstract void append(Level level, Element element, AnnotationMirror annotationMirror, String message);

	public abstract void close();

}
