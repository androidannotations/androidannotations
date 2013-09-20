package org.androidannotations.exception;

import javax.lang.model.element.Element;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = -1282996599471872615L;

	private Element element;

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
