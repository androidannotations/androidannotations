package org.androidannotations.logger.appender;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import org.androidannotations.logger.Level;

public class MessagerAppender extends Appender {

	private Messager messager;

	@Override
	public void open() {
		messager = processingEnv.getMessager();
	}

	@Override
	public void append(Level level, Element element, String message) {
		if (messager == null) {
			return;
		}

		Kind kind = resolveKind(level);
		messager.printMessage(kind, message, element);
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
