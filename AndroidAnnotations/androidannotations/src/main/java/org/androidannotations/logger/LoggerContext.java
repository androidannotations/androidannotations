package org.androidannotations.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoggerContext {

	private static LoggerContext INSTANCE = null;
	private static final Level DEFAULT_LEVEL = Level.DEBUG;

	private Level currentLevel = DEFAULT_LEVEL;
	private FileOutputStream outputStream;
	private File fileLog;

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

	private LoggerContext() {
		fileLog = new File("androidannotations.log");
		try {
			outputStream = new FileOutputStream(fileLog, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void writeLog(Level level, String loggerName, String message, Throwable thr, Object... args) {
		String log = Formatter.buildLog(level, loggerName, message, thr, args);

		try {
			outputStream.write(log.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}

}
