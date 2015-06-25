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
package org.androidannotations.exception;

import javax.lang.model.element.Element;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = -1282996599471872615L;

	private final Element element;

	public ProcessingException(Throwable cause, Element element) {
		super(cause);
		this.element = element;
	}

	public ProcessingException(String message, Throwable cause, Element element) {
		super(message, cause);
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

}
