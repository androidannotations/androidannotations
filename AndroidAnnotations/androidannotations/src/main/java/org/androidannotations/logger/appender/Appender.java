package org.androidannotations.logger.appender;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.logger.Level;

public abstract class Appender {

	protected ProcessingEnvironment processingEnv;

	public void setProcessingEnv(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	public abstract void open();

	public abstract void append(Level level, Element element, String message);

	public abstract void close();

}
