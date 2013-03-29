/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.generation;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.androidannotations.processing.OriginatingElements;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

public class SourceCodewriter extends CodeWriter {

	private final Filer filer;
	private final Messager message;

	private static final VoidOutputStream VOID_OUTPUT_STREAM = new VoidOutputStream();
	private OriginatingElements originatingElements;

	private static class VoidOutputStream extends OutputStream {
		@Override
		public void write(int arg0) throws IOException {
			// Do nothing
		}
	}

	public SourceCodewriter(Filer filer, Messager message, OriginatingElements originatingElements) {
		this.filer = filer;
		this.message = message;
		this.originatingElements = originatingElements;
	}

	@Override
	public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
		String qualifiedClassName = toQualifiedClassName(pkg, fileName);
		message.printMessage(Kind.NOTE, "Generating source file: " + qualifiedClassName);

		Element[] classOriginatingElements = originatingElements.getClassOriginatingElements(qualifiedClassName);

		try {
			JavaFileObject sourceFile;

			if (classOriginatingElements.length == 0) {
				message.printMessage(Kind.NOTE, "Generating class with no originating element: " + qualifiedClassName);
			}

			sourceFile = filer.createSourceFile(qualifiedClassName, classOriginatingElements);

			return sourceFile.openOutputStream();
		} catch (FilerException e) {
			message.printMessage(Kind.NOTE, "Could not generate source file for " + qualifiedClassName + ", message: " + e.getMessage());
			/*
			 * This exception is expected, when some files are created twice. We
			 * cannot delete existing files, unless using a dirty hack. Files a
			 * created twice when the same file is created from different
			 * annotation rounds. Happens when renaming classes, and for
			 * Background executor. It also probably means I didn't fully
			 * understand how annotation processing works. If anyone can point
			 * me out...
			 */
			return VOID_OUTPUT_STREAM;
		}
	}

	private String toQualifiedClassName(JPackage pkg, String fileName) {
		int suffixPosition = fileName.lastIndexOf('.');
		String className = fileName.substring(0, suffixPosition);

		String qualifiedClassName = pkg.name() + "." + className;
		return qualifiedClassName;
	}

	@Override
	public void close() throws IOException {
	}
}
