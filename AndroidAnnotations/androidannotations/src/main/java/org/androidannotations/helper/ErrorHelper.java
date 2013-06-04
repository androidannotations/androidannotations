package org.androidannotations.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.exception.ProcessingException;

public class ErrorHelper {

	public String getErrorMessage(ProcessingException e) {
		String errorMessage = "Unexpected error. Please report an issue on AndroidAnnotations " + AndroidAnnotationProcessor.ANDROIDANNOTATION_VERSION + ", with the following content and tell us if you can reproduce it or not. The error was thrown on:\n";
		if (e.getElement() != null) {
			errorMessage += elementFullString(e.getElement()) + "\n";
		}
		errorMessage += "compiled with " + getJavaCompilerVersion() + "\n";
		errorMessage += "with stacktrace: " + stackTraceToString(e.getCause());
		return errorMessage;
	}

	private String elementFullString(Element element) {
		String result = "";
		List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
		for (AnnotationMirror annotation : annotations) {
			result += annotationFullString(annotation) + "\n";
		}
		return result + element.toString();
	}

	private String annotationFullString(AnnotationMirror annotation) {
		String result = annotation.toString();

		Map<? extends ExecutableElement, ? extends AnnotationValue> fields = annotation.getElementValues();
		if (fields != null) {
			result += "(";
			Set<? extends ExecutableElement> fieldKeys = fields.keySet();
			int i = 0;
			for (ExecutableElement fieldKey : fieldKeys) {
				result += fieldKey.getSimpleName().toString() + "=" + fields.get(fieldKey).getValue().toString();
				if (++i < fieldKeys.size()) {
					result += ", ";
				}
			}
			result += ")";
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
