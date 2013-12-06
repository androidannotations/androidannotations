package org.androidannotations.logger.appender;

import javax.lang.model.element.Element;

import org.androidannotations.logger.Level;

public class ConsoleAppender extends Appender {

	@Override
	public void open() {
	}

	@Override
	public void append(Level level, Element element, String message) {
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
