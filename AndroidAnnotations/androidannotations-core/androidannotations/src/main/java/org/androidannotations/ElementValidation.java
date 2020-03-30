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
package org.androidannotations;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class ElementValidation {

	private String annotationName;
	private Element element;
	private boolean isValid = true;
	private List<Error> errors = new ArrayList<>();
	private List<String> warnings = new ArrayList<>();

	public ElementValidation(String annotationName, Element element) {
		this.annotationName = annotationName;
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public void invalidate() {
		isValid = false;
	}

	/**
	 * Method to call when an annotation is not valid
	 *
	 * @param error
	 *            The message of the message. If it contains %s, it will be replaced
	 *            by the name of the annotation.
	 */
	public void addError(String error) {
		addError(element, error);
	}

	public void addError(Element element, String error) {
		isValid = false;
		this.errors.add(new Error(element, String.format(error, annotationName)));
	}

	public boolean isValid() {
		return isValid;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void addWarning(String error) {
		warnings.add(String.format(error, annotationName));
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public String getAnnotationName() {
		return annotationName;
	}

	public AnnotationMirror getAnnotationMirror() {
		List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
			if (annotationElement.getQualifiedName().toString().equals(annotationName)) {
				return annotationMirror;
			}
		}
		return null;
	}

	public static class Error {
		private Element element;
		private String message;

		public Error(Element element, String message) {
			this.element = element;
			this.message = message;
		}

		public Element getElement() {
			return element;
		}

		public String getMessage() {
			return message;
		}
	}
}
