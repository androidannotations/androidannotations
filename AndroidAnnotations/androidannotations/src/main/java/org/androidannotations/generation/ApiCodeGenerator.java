package org.androidannotations.generation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import org.androidannotations.processing.OriginatingElementsHolder;

public class ApiCodeGenerator {

	private final static byte[] BUFFER = new byte[4096];

	private final Filer filer;

	public ApiCodeGenerator(Filer filer) {
		this.filer = filer;
	}

	public void writeApiClasses(Set<Class<?>> apiClassesToGenerate, OriginatingElementsHolder originatingElementsHolder) {

		for (Class<?> apiClassToGenerate : apiClassesToGenerate) {

			String cannonicalApiClassName = apiClassToGenerate.getCanonicalName();

			String apiClassFileName = cannonicalApiClassName.replace(".", "/") + ".java";

			InputStream apiClassStream = getClass().getClassLoader().getResourceAsStream(apiClassFileName);
			try {

				if (apiClassStream == null) {
					// The processor is not executed from a Jar. In this case,
					// we have to add a magic '/'
					apiClassStream = getClass().getClassLoader().getResourceAsStream('/' + apiClassFileName);
				}

				Element[] originatingElements = originatingElementsHolder.getOriginatingElements(cannonicalApiClassName);

				JavaFileObject targetedClassFile;
				if (originatingElements == null) {
					targetedClassFile = filer.createSourceFile(cannonicalApiClassName);
				} else {
					targetedClassFile = filer.createSourceFile(cannonicalApiClassName, originatingElements);
				}

				OutputStream classFileOutputStream = targetedClassFile.openOutputStream();
				copyStream(apiClassStream, classFileOutputStream);
				classFileOutputStream.close();

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		int read;
		while ((read = input.read(BUFFER)) != -1) {
			output.write(BUFFER, 0, read);
		}
	}

}
