package org.androidannotations.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;

import org.androidannotations.helper.FileHelper;

public class Appender {

	private static final String DEFAULT_FILENAME = "androidannotations.log";

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
		Messager messager = processingEnv.getMessager();
		if (processingEnv.getOptions().containsKey(LoggerContext.LOG_FILE_OPTION)) {
			file = resolveLogFileInSpecifiedPath();
		} else {
			file = resolveLogFileInParentsDirectories();
		}

		if (file == null) {
			messager.printMessage(Kind.WARNING, "Can't resolve log file");
		} else {
			messager.printMessage(Kind.NOTE, "Resolve log file to " + file.getAbsolutePath());
		}
	}

	private File resolveLogFileInSpecifiedPath() {
		File outputDirectory = FileHelper.resolveOutputDirectory(processingEnv);

		String path = processingEnv.getOptions().get(LoggerContext.LOG_FILE_OPTION);
		path = path.replace("{outputFolder}", outputDirectory.getAbsolutePath());

		return new File(path);
	}

	private File resolveLogFileInParentsDirectories() {
		File outputDirectory = FileHelper.resolveOutputDirectory(processingEnv);
		return new File(outputDirectory, DEFAULT_FILENAME);
	}

	public boolean isFileOpened() {
		return outputStream != null;
	}

	public void setProcessingEnv(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

}
