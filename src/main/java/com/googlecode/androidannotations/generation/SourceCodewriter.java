package com.googlecode.androidannotations.generation;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

public class SourceCodewriter extends CodeWriter {
	
	private final Filer filer;
	
	public SourceCodewriter(Filer filer) {
		this.filer = filer;
	}

	@Override
	public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
		String qualifiedClassName = toQualifiedClassName(pkg, fileName);
		
		JavaFileObject sourceFile = filer.createSourceFile(qualifiedClassName);
		
		return sourceFile.openOutputStream();
	}

	private String toQualifiedClassName(JPackage pkg, String fileName) {
		int suffixPosition = fileName.lastIndexOf('.');
		String className = fileName.substring(0, suffixPosition);
		
		String qualifiedClassName = pkg.name() + "."+className;
		return qualifiedClassName;
	}

	@Override
	public void close() throws IOException {}
}