package org.androidannotations.generation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class ApiCodeGenerator {

	private final static byte[] BUFFER = new byte[4096];

	private final Filer filer;
	private final Messager messager;

	public ApiCodeGenerator(Filer filer, Messager messager) {
		this.filer = filer;
		this.messager = messager;
	}

	public void writeApiClasses(Set<Class<?>> apiClassesToGenerate, Map<String, List<Element>> originatingElementsByGeneratedClassQualifiedName) {

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

				List<Element> originatingElements = originatingElementsByGeneratedClassQualifiedName.get(cannonicalApiClassName);

				JavaFileObject targetedClassFile;
				if (originatingElements == null) {
					targetedClassFile = filer.createSourceFile(cannonicalApiClassName);
				} else {
					targetedClassFile = filer.createSourceFile(cannonicalApiClassName, originatingElements.toArray(new Element[originatingElements.size()]));
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

	private void printError(String message) {
		messager.printMessage(Diagnostic.Kind.ERROR, message);
	}

}
