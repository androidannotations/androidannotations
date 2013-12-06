package org.androidannotations.logger;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import org.androidannotations.logger.appender.Appender;
import org.androidannotations.logger.appender.FileAppender;
import org.androidannotations.logger.appender.MessagerAppender;

public class LoggerContext {

	public static final String LOG_FILE_OPTION = "logFile";
	public static final String LOG_LEVEL_OPTION = "logLevel";
	private static LoggerContext INSTANCE = null;
	private static final Level DEFAULT_LEVEL = Level.DEBUG;

	private Level currentLevel = DEFAULT_LEVEL;
	private List<Appender> appenders = new ArrayList<Appender>();
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
		// appenders.add(new ConsoleAppender());
		appenders.add(new FileAppender());
		appenders.add(new MessagerAppender());
	}

	public void writeLog(Level level, String loggerName, String message, Element element, AnnotationMirror annotationMirror, Throwable thr, Object... args) {
		String log = formatter.buildLog(level, loggerName, message, thr, args);
		for (Appender appender : appenders) {
			appender.append(level, element, annotationMirror, log);
		}
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}

	public void setProcessingEnv(ProcessingEnvironment processingEnv) {
		for (Appender appender : appenders) {
			appender.setProcessingEnv(processingEnv);
			appender.open();
		}

		resolveLogLevel(processingEnv);
	}

	public void close() {
		for (Appender appender : appenders) {
			appender.close();
		}
	}

	private void resolveLogLevel(ProcessingEnvironment processingEnv) {
		if (processingEnv.getOptions().containsKey(LoggerContext.LOG_LEVEL_OPTION)) {
			Level logLevel = Level.parse(processingEnv.getOptions().get(LoggerContext.LOG_LEVEL_OPTION));
			setCurrentLevel(logLevel);
		}
	}

}
