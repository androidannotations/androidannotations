package com.googlecode.androidannotations.generation;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.tools.JavaFileObject;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

public class SourceCodewriter extends CodeWriter {

	private final Filer filer;

	private static final VoidOutputStream VOID_OUTPUT_STREAM = new VoidOutputStream();

	private static class VoidOutputStream extends OutputStream {
		@Override
		public void write(int arg0) throws IOException {
			// Do nothing
		}
	}

	public SourceCodewriter(Filer filer) {
		this.filer = filer;
	}

	@Override
	public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
		String qualifiedClassName = toQualifiedClassName(pkg, fileName);
		try {
			JavaFileObject sourceFile = filer.createSourceFile(qualifiedClassName);
			return sourceFile.openOutputStream();
		} catch (FilerException e) {
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