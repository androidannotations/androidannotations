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
package org.androidannotations.helper;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

import org.androidannotations.exception.ProcessingException;

public class ErrorHelper {

	public String getErrorMessage(ProcessingEnvironment processingEnv, ProcessingException e, String aaVersion) {
		String errorMessage = "Unexpected error in AndroidAnnotations " + aaVersion + "!\n"
				+ "You should check if there is already an issue about it on https://github.com/excilys/androidannotations/search?q=" + urlEncodedErrorMessage(e) + "&type=Issues\n"
				+ "If none exists, please open a new one with the following content and tell us if you can reproduce it or not. "
				+ "Don't forget to give us as much information as you can (like parts of your code in failure).\n";
		errorMessage += "Java version: " + getJavaCompilerVersion() + "\n";
		errorMessage += "Javac processors options: " + annotationProcessorOptions(processingEnv) + "\n";
		errorMessage += "Stacktrace: " + stackTraceToString(e.getCause());

		Element element = e.getElement();
		if (element != null) {
			errorMessage += "Thrown from: " + elementContainer(element) + "\n";
			errorMessage += "Element (" + element.getClass().getSimpleName() + "): " + elementFullString(processingEnv, element) + "\n";
		}

		return errorMessage;
	}

	private String elementFullString(ProcessingEnvironment processingEnv, Element element) {
		Elements elementUtils = processingEnv.getElementUtils();
		CharArrayWriter writer = new CharArrayWriter();
		elementUtils.printElements(writer, element);
		return writer.toString();
	}

	private String elementContainer(Element element) {
		Element enclosingElement = element.getEnclosingElement();
		return enclosingElement != null ? enclosingElement.toString() : "";
	}

	private String annotationProcessorOptions(ProcessingEnvironment processingEnv) {
		Map<String, String> options = processingEnv.getOptions();
		Set<Entry<String, String>> optionsEntries = options.entrySet();

		String result = "";
		for (Entry<String, String> optionEntry : optionsEntries) {
			result += optionEntry.getKey() + "=" + optionEntry.getValue() + ", ";
		}
		return result.length() > 2 ? result.substring(0, result.length() - 2) : result;
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
			// ignored
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// cannot help this
				}
			}
		}
		return "unknown";
	}

	private String urlEncodedErrorMessage(Throwable e) {
		try {
			return URLEncoder.encode(e.getCause().getClass().getName(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return "";
		}
	}

	private String stackTraceToString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		e.printStackTrace(pw);
		return writer.toString();
	}

}
