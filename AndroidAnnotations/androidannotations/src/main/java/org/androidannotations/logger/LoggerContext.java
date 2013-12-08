package org.androidannotations.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import org.androidannotations.logger.appender.Appender;
import org.androidannotations.logger.appender.ConsoleAppender;
import org.androidannotations.logger.appender.FileAppender;
import org.androidannotations.logger.appender.MessagerAppender;

public class LoggerContext {

	public static final String LOG_FILE_OPTION = "logFile";
	public static final String LOG_LEVEL_OPTION = "logLevel";
	public static final String LOG_APPENDER_CONSOLE = "logAppenderConsole";
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
		addConsoleAppender(processingEnv);
	}

	public void close() {
		for (Appender appender : appenders) {
			appender.close();
		}
	}

	private void resolveLogLevel(ProcessingEnvironment processingEnv) {
		Map<String, String> options = processingEnv.getOptions();
		if (options.containsKey(LOG_LEVEL_OPTION)) {
			Level logLevel = Level.parse(options.get(LOG_LEVEL_OPTION));
			setCurrentLevel(logLevel);
		}
	}

	private void addConsoleAppender(ProcessingEnvironment processingEnv) {
		Map<String, String> options = processingEnv.getOptions();
		if (options.containsKey(LOG_APPENDER_CONSOLE)) {
			if (Boolean.parseBoolean(options.get(LOG_APPENDER_CONSOLE))) {
				appenders.add(new ConsoleAppender());
			}
		}
	}

}
