package org.androidannotations.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;

public class Appender {

	private File file;
	private FileOutputStream outputStream;
	private ProcessingEnvironment processingEnv;

	public synchronized void openFile() {
		try {
			outputStream = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized void closeFile() {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
		}
	}

	public synchronized void append(String log) {
		if (!isFileOpened()) {
			openFile();
		}

		try {
			outputStream.write(log.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resolveLogFile() {
		if (file == null) {
			if (processingEnv.getOptions().containsKey(LoggerContext.LOG_FILE_OPTION)) {
				file = resolveLogFileInSpecifiedPath();
			} else {
				file = resolveLogFileInParentsDirectories();
			}
		}
	}

	private File resolveLogFileInSpecifiedPath() {
		String path = processingEnv.getOptions().get(LoggerContext.LOG_FILE_OPTION);
		return new File(path);
	}

	private File resolveLogFileInParentsDirectories() {
		// TODO

		if (file == null) {
			Messager messager = processingEnv.getMessager();
			messager.printMessage(Kind.ERROR, "Can't resolve log file path");
		}
		return null;
	}

	public boolean isFileOpened() {
		return outputStream != null;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setProcessingEnv(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

}
