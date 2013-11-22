package org.androidannotations.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Formatter {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.S");
	private static final String ARGS_PATTERN = "{}";
	private static final int ARGS_PATTERN_LENGTH = ARGS_PATTERN.length();

	public String buildLog(Level level, String loggerName, String message, Throwable thr, Object... args) {
		String fullMessage = buildFullMessage(message, args);
		StringBuilder stringBuilder = new StringBuilder(fullMessage.length());

		stringBuilder.append(DATE_FORMAT.format(new Date())) //
				.append(" [").append(Thread.currentThread().getName()).append("]") //
				.append(" ").append(level.name) //
				.append(" ").append(loggerName) //
				.append(" - ").append(fullMessage);

		// Stacktrace
		if (thr != null) {
			stringBuilder.append('\n').append(stackTraceToString(thr));
		}

		stringBuilder.append('\n');

		return stringBuilder.toString();
	}

	public String buildFullMessage(String message, Object... args) {
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

	private String formatArgument(Object arg) {
		if (arg != null && arg.getClass().isArray()) {
			return Arrays.toString((Object[]) arg);
		}
		return arg.toString();
	}

	private String stackTraceToString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		e.printStackTrace(pw);
		return writer.toString();
	}

}
