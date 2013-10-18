package org.androidannotations.logger;

public class Logger {

	private final String name;

	public Logger(String name) {
		this.name = name;
	}

	public void trace(String message, Object... args) {
		log(Level.TRACE, message, null, args);
	}

	public void debug(String message, Object... args) {
		log(Level.DEBUG, message, null, args);
	}

	public void info(String message, Object... args) {
		log(Level.INFO, message, null, args);
	}

	public void warn(String message, Object... args) {
		log(Level.WARN, message, null, args);
	}

	public void warn(String message, Throwable thr, Object... args) {
		log(Level.WARN, message, thr, args);
	}

	public void error(String message, Object... args) {
		log(Level.ERROR, message, null, args);
	}

	public void error(String message, Throwable thr, Object... args) {
		log(Level.ERROR, message, thr, args);
	}

	private void log(Level level, String message, Throwable thr, Object... args) {
		if (LoggerContext.getInstance().getCurrentLevel().isGreaterOrEqual(level)) {
			return;
		}

		LoggerContext.getInstance().writeLog(level, name, message, thr, args);
	}

}
