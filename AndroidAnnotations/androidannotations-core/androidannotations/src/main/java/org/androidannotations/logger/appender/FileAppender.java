/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.logger.appender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import org.androidannotations.helper.FileHelper;
import org.androidannotations.logger.Level;
import org.androidannotations.logger.LoggerContext;
import org.androidannotations.logger.formatter.FormatterFull;

public class FileAppender extends Appender {

	private static final String DEFAULT_FILENAME = "androidannotations.log";

	private File file;
	private FileOutputStream outputStream;

	public FileAppender() {
		super(new FormatterFull());
	}

	@Override
	public synchronized void open() {
		if (!isStreamOpened()) {
			try {
				outputStream = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void close() {
		if (isStreamOpened()) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
		}
	}

	@Override
	public synchronized void append(Level level, Element element, AnnotationMirror annotationMirror, String message) {
		if (isStreamOpened()) {
			try {
				message += "\n";
				outputStream.write(message.getBytes());
				outputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setProcessingEnv(ProcessingEnvironment processingEnv) {
		super.setProcessingEnv(processingEnv);
		resolveLogFile();
	}

	private void resolveLogFile() {
		String logFile = optionsHelper.getLogFile();
		if (logFile != null) {
			file = resolveLogFileInSpecifiedPath(logFile);
		} else {
			file = resolveLogFileInParentsDirectories();
		}

		Level logLevel = LoggerContext.getInstance().getCurrentLevel();
		Messager messager = processingEnv.getMessager();
		if (file == null) {
			if (Level.WARN.isGreaterOrEquals(logLevel)) {
				messager.printMessage(Kind.WARNING, "Can't resolve log file");
			}
		} else if (Level.INFO.isGreaterOrEquals(logLevel)) {
			messager.printMessage(Kind.NOTE, "Resolve log file to " + file.getAbsolutePath());
		}
	}

	private File resolveLogFileInSpecifiedPath(String logFile) {
		File outputDirectory = FileHelper.resolveOutputDirectory(processingEnv);
		logFile = logFile.replace("{outputFolder}", outputDirectory.getAbsolutePath());
		return new File(logFile);
	}

	private File resolveLogFileInParentsDirectories() {
		File outputDirectory = FileHelper.resolveOutputDirectory(processingEnv);
		return new File(outputDirectory, DEFAULT_FILENAME);
	}

	private boolean isStreamOpened() {
		return outputStream != null;
	}

}
