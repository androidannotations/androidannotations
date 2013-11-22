package org.androidannotations.logger;

import javax.annotation.processing.ProcessingEnvironment;

public class LoggerContext {

	public static final String LOG_FILE_OPTION = "logFile";
	public static final String LOG_LEVEL_OPTION = "logLevel";
	private static LoggerContext INSTANCE = null;
	private static final Level DEFAULT_LEVEL = Level.DEBUG;

	private Level currentLevel = DEFAULT_LEVEL;
	private Appender appender = new Appender();
	private Formatter formatter = new Formatter();

	public static LoggerContext getInstance() {
		if (INSTANCE == null) {
			synchronized (LoggerContext.class) {
				if (INSTANCE == null) {
					INSTANCE = new LoggerContext();
				}
			}
		}
		return INSTANCE;
	}

	LoggerContext() {
	}

	public void writeLog(Level level, String loggerName, String message, Throwable thr, Object... args) {
		String log = formatter.buildLog(level, loggerName, message, thr, args);
		appender.append(log);
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}

	public void setProcessingEnv(ProcessingEnvironment processingEnv) {
		appender.setProcessingEnv(processingEnv);
		appender.resolveLogFile();

		resolveLogLevel(processingEnv);
	}

	public void close() {
		appender.closeFile();
	}

	private void resolveLogLevel(ProcessingEnvironment processingEnv) {
		if (processingEnv.getOptions().containsKey(LoggerContext.LOG_LEVEL_OPTION)) {
			Level logLevel = Level.parse(processingEnv.getOptions().get(LoggerContext.LOG_LEVEL_OPTION));
			setCurrentLevel(logLevel);
		}
	}

}
