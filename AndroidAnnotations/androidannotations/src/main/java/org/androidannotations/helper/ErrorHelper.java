package org.androidannotations.helper;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

import org.androidannotations.exception.ProcessingException;

public class ErrorHelper {

	public String getErrorMessage(ProcessingEnvironment processingEnv, ProcessingException e, String aaVersion) {
		String errorMessage = "Unexpected error. Please report an issue on AndroidAnnotations " + aaVersion + ", with the following content and tell us if you can reproduce it or not. The error was thrown on:\n";
		if (e.getElement() != null) {
			errorMessage += elementFullString(processingEnv, e.getElement()) + "\n";
		}
		errorMessage += "compiled with " + getJavaCompilerVersion() + "\n";
		errorMessage += "with stacktrace: " + stackTraceToString(e.getCause());
		return errorMessage;
	}

	private String elementFullString(ProcessingEnvironment processingEnv, Element element) {
		Elements elementUtils = processingEnv.getElementUtils();
		CharArrayWriter writer = new CharArrayWriter();
		elementUtils.printElements(writer, element);
		String result = writer.toString();

		Element enclosingElement = element.getEnclosingElement();
		if (enclosingElement != null) {
			result = result + "\nin: " + enclosingElement.toString();
		}
		return result;
	}

	private String getJavaCompilerVersion() {
		ProcessBuilder pb = new ProcessBuilder("javac", "-version");
		pb.redirectErrorStream(true);

		BufferedReader in = null;
		try {
			Process process = pb.start();
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String buffer = in.readLine();
			process.waitFor();
			return buffer;
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return "unknown";
	}

	private String stackTraceToString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		e.printStackTrace(pw);
		return writer.toString();
	}

}
