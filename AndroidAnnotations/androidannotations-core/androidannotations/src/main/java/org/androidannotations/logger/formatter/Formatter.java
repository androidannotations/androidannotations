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
package org.androidannotations.logger.formatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.androidannotations.logger.Level;
import org.androidannotations.logger.Logger;

public abstract class Formatter {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.S");
	private static final String ARGS_PATTERN = "{}";
	private static final int ARGS_PATTERN_LENGTH = ARGS_PATTERN.length();

	public abstract String buildLog(Level level, String loggerName, String message, Throwable thr, Object... args);

	protected String getCurrentThread() {
		return Thread.currentThread().getName();
	}

	protected String getTime() {
		return DATE_FORMAT.format(new Date());
	}

	protected String buildFullMessage(String message, Object... args) {
		StringBuilder stringBuilder = new StringBuilder(message.length());
		int lastIndex = 0;
		int argIndex = 0;

		while (true) {
			int argPos = message.indexOf(ARGS_PATTERN, lastIndex);
			if (argPos == -1) {
				break;
			}

			stringBuilder.append(message.substring(lastIndex, argPos));

			lastIndex = argPos + ARGS_PATTERN_LENGTH;

			// add the argument, if we still have any
			if (argIndex < args.length) {
				stringBuilder.append(formatArgument(args[argIndex]));
				argIndex++;
			}
		}

		stringBuilder.append(message.substring(lastIndex));

		return stringBuilder.toString();
	}

	protected String formatArgument(Object arg) {
		if (arg != null && arg.getClass().isArray()) {
			return Arrays.toString((Object[]) arg);
		}
		return arg.toString();
	}

	protected String stackTraceToString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		e.printStackTrace(pw);
		return writer.toString();
	}

	protected int getCallerLineNumber() {
		boolean previousWasLogger = false;
		String loggerClassName = Logger.class.getCanonicalName();

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			if (stackTraceElement.getClassName().equals(loggerClassName)) {
				previousWasLogger = true;
			} else if (previousWasLogger) {
				return stackTraceElement.getLineNumber();
			}
		}
		return -1;
	}
}
