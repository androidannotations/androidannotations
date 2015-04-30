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
package org.androidannotations.generation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;
import org.androidannotations.process.OriginatingElements;

public class ApiCodeGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiCodeGenerator.class);
	private static final byte[] BUFFER = new byte[4096];

	private static void copyStream(InputStream input, OutputStream output) throws IOException {
		int read;
		while ((read = input.read(BUFFER)) != -1) {
			output.write(BUFFER, 0, read);
		}
	}

	private final Filer filer;

	public ApiCodeGenerator(Filer filer) {
		this.filer = filer;
	}

	public void writeApiClasses(Set<Class<?>> apiClassesToGenerate, OriginatingElements originatingElements) {

		LOGGER.info("Writting following API classes in project: {}", apiClassesToGenerate);

		for (Class<?> apiClassToGenerate : apiClassesToGenerate) {

			String canonicalApiClassName = apiClassToGenerate.getCanonicalName();

			String apiClassFileName = canonicalApiClassName.replace(".", "/") + ".java";

			InputStream apiClassStream = getClass().getClassLoader().getResourceAsStream(apiClassFileName);
			try {

				if (apiClassStream == null) {
					/*
					 * This happens when in AA dev environment, when the
					 * processor classes are not coming from a jar
					 */
					apiClassStream = getClass().getClassLoader().getResourceAsStream('/' + apiClassFileName);
				}

				Element[] apiClassOriginatingElements = originatingElements.getClassOriginatingElements(canonicalApiClassName);

				JavaFileObject targetedClassFile;

				try {
					if (apiClassOriginatingElements == null) {
						targetedClassFile = filer.createSourceFile(canonicalApiClassName);
					} else {
						targetedClassFile = filer.createSourceFile(canonicalApiClassName, apiClassOriginatingElements);
					}

					OutputStream classFileOutputStream = targetedClassFile.openOutputStream();
					copyStream(apiClassStream, classFileOutputStream);
					classFileOutputStream.close();

				} catch (FilerException e) {
					// This exception is thrown when we are trying to generate
					// an already generated file. This is happening on an
					// incremental build.
					// Unfortunately there is no way to check if a file has
					// already been generated.
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
