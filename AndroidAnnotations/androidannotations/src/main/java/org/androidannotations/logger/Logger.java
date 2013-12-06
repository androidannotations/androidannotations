package org.androidannotations.logger;

import javax.lang.model.element.Element;

public class Logger {

	private final LoggerContext loggerContext;
	private final String name;

	public Logger(LoggerContext loggerContext, String name) {
		this.loggerContext = loggerContext;
		this.name = name;
	}

	public void trace(String message, Object... args) {
		trace(message, null, args);
	}

	public void trace(String message, Element element, Object... args) {
		log(Level.TRACE, message, element, null, args);
	}

	public void debug(String message, Object... args) {
		debug(message, null, args);
	}

	public void debug(String message, Element element, Object... args) {
		log(Level.DEBUG, message, element, null, args);
	}

	public void info(String message, Object... args) {
		info(message, null, args);
	}

	public void info(String message, Element element, Object... args) {
		log(Level.INFO, message, element, null, args);
	}

	public void warn(String message, Object... args) {
		warn(message, null, null, args);
	}

	public void warn(String message, Element element, Object... args) {
		warn(message, element, null, args);
	}

	public void warn(String message, Element element, Throwable thr, Object... args) {
		log(Level.WARN, message, element, thr, args);
	}

	public void error(String message, Object... args) {
		error(message, null, null, args);
	}

	public void error(String message, Element element, Object... args) {
		error(message, element, null, args);
	}

	public void error(String message, Element element, Throwable thr, Object... args) {
		log(Level.ERROR, message, element, thr, args);
	}

	public boolean isLoggable(Level level) {
		return level.isGreaterOrEquals(loggerContext.getCurrentLevel());
	}

	private void log(Level level, String message, Element element, Throwable thr, Object... args) {
		if (!isLoggable(level)) {
			return;
		}

		loggerContext.writeLog(level, name, message, element, thr, args);
	}

}
