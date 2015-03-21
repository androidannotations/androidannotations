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
package org.androidannotations.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Based on http://code.google.com/p/acris/wiki/AnnotationProcessing_Testing
 */
public class ProcessorTestHelper {

	public static class CompileResult {
		private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

		public CompileResult(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
			this.diagnostics = diagnostics;
		}
	}

	private static final String TEST_SOURCE_FOLDER = "src/test/java";
	private static final String MAIN_SOURCE_FOLDER = "src/main/java";
	protected static final String SOURCE_FILE_SUFFIX = ".java";
	protected static final String OUTPUT_DIRECTORY = "target/generated-test";

	public static void assertGeneratedClassMatches(File output, String value) {
		String[] outputContent = getContents(output);
		for (String line : outputContent) {
			if (line.matches(value)) {
				return;
			}
		}
		fail("Expected value \"" + value + "\" couldn't be found in file " + output.getAbsolutePath());
	}

	public static void assertGeneratedClassDoesntMatches(File output, String value) {
		String[] outputContent = getContents(output);
		for (String line : outputContent) {
			if (line.matches(value)) {
				fail("Value \"" + value + "\" shouldn't be in file " + output.getAbsolutePath());
			}
		}
	}

	public static void assertGeneratedClassContains(File output, String[] codeFragment) {
		assertTrue("Code fragment \"" + join(codeFragment) + "\" should be in file " + output.getAbsolutePath(),
				Collections.indexOfSubList(Arrays.asList(getContents(output)), Arrays.asList(codeFragment)) != -1);
	}

	public static void assertGeneratedClassDoesNotContain(File output, String[] codeFragment) {
		assertTrue("Code fragment \"" + join(codeFragment) + "\" should not be in file " + output.getAbsolutePath(),
				Collections.indexOfSubList(Arrays.asList(getContents(output)), Arrays.asList(codeFragment)) == -1);
	}

	public static void assertOutput(File expectedResult, File output) {
		String[] expectedContent = getContents(expectedResult);
		String[] outputContent = getContents(output);
		assertEquals(expectedContent.length, outputContent.length);

		for (int i = 0; i < expectedContent.length; i++) {
			assertEquals(expectedContent[i].trim(), outputContent[i].trim());
		}
	}

	public static void assertClassSourcesGeneratedToOutput(Class<?> clazz) {

		String canonicalName = clazz.getCanonicalName();
		String filePath = canonicalName.replace(".", "/").concat(".java");

		File generatedSourcesDir = new File(OUTPUT_DIRECTORY);
		File generatedSourceFile = new File(generatedSourcesDir, filePath);

		File sourcesDir = new File(MAIN_SOURCE_FOLDER);
		File expectedResult = new File(sourcesDir, filePath);

		assertOutput(expectedResult, generatedSourceFile);
	}

	public static void assertClassSourcesNotGeneratedToOutput(Class<?> clazz) {

		String canonicalName = clazz.getCanonicalName();
		String filePath = canonicalName.replace(".", "/").concat(".java");

		File generatedSourcesDir = new File(OUTPUT_DIRECTORY);
		File output = new File(generatedSourcesDir, filePath);

		assertFalse(output.exists());
	}

	public static void assertCompilationSuccessful(CompileResult result) {
		for (Diagnostic<? extends JavaFileObject> diagnostic : result.diagnostics) {
			assertFalse("Expected no errors, found " + diagnostic, diagnostic.getKind().equals(Kind.ERROR));
		}

	}

	public static void assertCompilationError(CompileResult result) {
		for (Diagnostic<? extends JavaFileObject> diagnostic : result.diagnostics) {
			if (diagnostic.getKind() == Kind.ERROR) {
				return;
			}
		}
		fail("Expected a compilation error, diagnostics: " + result.diagnostics);
	}

	public static void assertCompilationErrorWithNoSource(CompileResult result) {
		for (Diagnostic<? extends JavaFileObject> diagnostic : result.diagnostics) {
			if (diagnostic.getKind() == Kind.ERROR && diagnostic.getSource() == null) {
				return;
			}
		}
		fail("Expected a compilation error with no source, diagnostics: " + result.diagnostics);
	}

	public static void assertCompilationErrorCount(int expectedErrorCount, CompileResult result) {
		int errorCount = 0;
		for (Diagnostic<? extends JavaFileObject> diagnostic : result.diagnostics) {
			if (diagnostic.getKind() == Kind.ERROR) {
				errorCount++;
			}
		}

		if (errorCount != expectedErrorCount) {
			fail("Expected " + expectedErrorCount + " compilation error, found " + errorCount + " diagnostics: " + result.diagnostics);
		}
	}

	public static void assertCompilationErrorOn(File expectedErrorClassFile, String expectedContentInError, CompileResult result) throws IOException {
		assertCompilationDiagnostingOn(Kind.ERROR, expectedErrorClassFile, expectedContentInError, result);
	}

	public static void assertCompilationWarningOn(File expectedErrorClassFile, String expectedContentInError, CompileResult result) throws IOException {
		assertCompilationDiagnostingOn(Kind.WARNING, expectedErrorClassFile, expectedContentInError, result);
	}

	private static void assertCompilationDiagnostingOn(Kind expectedDiagnosticKind, File expectedErrorClassFile, String expectedContentInError, CompileResult result) throws IOException {

		String expectedErrorPath = expectedErrorClassFile.toURI().toString();
		for (Diagnostic<? extends JavaFileObject> diagnostic : result.diagnostics) {
			if (diagnostic.getKind() == expectedDiagnosticKind) {
				JavaFileObject source = diagnostic.getSource();
				if (source != null) {
					if (expectedErrorPath.endsWith(source.toUri().toString())) {

						CharSequence sourceContent = source.getCharContent(true);
						if (diagnostic.getPosition() != Diagnostic.NOPOS) {
							CharSequence contentInError = sourceContent.subSequence((int) diagnostic.getStartPosition(), (int) diagnostic.getEndPosition());
							if (contentInError.toString().contains(expectedContentInError)) {
								return;
							}
						}
					}
				}
			}
		}
		fail("Expected a compilation " + expectedDiagnosticKind + " in " + expectedErrorClassFile.toString() + " on " + expectedContentInError + ", diagnostics: " + result.diagnostics);
	}

	private static String[] getContents(File file) {
		List<String> content = new ArrayList<>();

		try (BufferedReader input = new BufferedReader(new FileReader(file))) {
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				content.add(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return content.toArray(new String[] {});
	}

	private final List<String> compilerOptions = new ArrayList<>();

	private final List<Class<? extends Processor>> processorsClasses = new ArrayList<>();

	public ProcessorTestHelper() {
		compilerOptions.add("-classpath");
		compilerOptions.add(getClassPath());
		compilerOptions.add("-s");
		String outputPath = ensureOutputDirectory().getAbsolutePath();
		compilerOptions.add(outputPath);
		compilerOptions.add("-d");
		compilerOptions.add(outputPath);
	}

	public File getOuputDirectory() {
		return ensureOutputDirectory();
	}

	public void addProcessor(Class<? extends Processor> processorClass) {
		processorsClasses.add(processorClass);
	}

	public void addProcessorParameter(String key, String value) {
		addCompilerOptions("-A" + key + "=" + value);
	}

	public final void addCompilerOptions(String... compilerOptions) {
		for (String compilerOption : compilerOptions) {
			this.compilerOptions.add(compilerOption);
		}
	}

	public String toPath(Package packageName) {
		return toPath(packageName.getName());
	}

	public String toPath(String packageName) {
		return packageName.replace(".", "/");
	}

	/**
	 * Attempts to compile the given compilation units using the Java Compiler
	 * API.
	 * <p>
	 * The compilation units and all their dependencies are expected to be on
	 * the classpath.
	 *
	 * @param compilationUnits
	 *            the classes to compile
	 * @return the {@link Diagnostic diagnostics} returned by the compilation,
	 *         as demonstrated in the documentation for {@link JavaCompiler}
	 * @see #compileFiles(String...)
	 */
	public CompileResult compileFiles(Type... compilationUnits) {
		assert compilationUnits != null;

		List<File> files = new ArrayList<>();

		addCollection(files, compilationUnits);

		return compileFiles(files);
	}

	public CompileResult compileFiles(Object... elements) {
		assert elements != null;

		List<File> files = new ArrayList<>();
		for (Object element : elements) {
			if (element instanceof Type) {
				addCollection(files, (Type) element);
			} else if (element instanceof String) {
				files.add(new File((String) element));
			}
		}
		return compileFiles(files);
	}

	public CompileResult compileFiles(File... compilationUnits) {
		return compileFiles(Arrays.asList(compilationUnits));
	}

	public CompileResult compileFiles(Collection<File> compilationUnits) {
		DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, null, null)) {
			CompilationTask task = compiler.getTask(null, fileManager, diagnosticCollector, compilerOptions, null, fileManager.getJavaFileObjectsFromFiles(compilationUnits));

			List<Processor> processors = new ArrayList<>();

			for (Class<? extends Processor> processorClass : processorsClasses) {
				try {
					processors.add(processorClass.newInstance());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			task.setProcessors(processors);

			task.call();
		} catch (IOException e) {

			// we should always be able to close the manager
		}

		return new CompileResult(diagnosticCollector.getDiagnostics());
	}

	public void assertCompilationErrorOn(Class<?> expectedErrorClass, String expectedContentInError, CompileResult result) throws IOException {
		assertCompilationErrorOn(toFile(expectedErrorClass), expectedContentInError, result);
	}

	public void assertCompilationWarningOn(Class<?> expectedErrorClass, String expectedContentInError, CompileResult result) throws IOException {
		assertCompilationWarningOn(toFile(expectedErrorClass), expectedContentInError, result);
	}

	private File ensureOutputDirectory() {
		File outputDir = new File(OUTPUT_DIRECTORY);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		return outputDir;
	}

	public void ensureOutputDirectoryIsEmpty() {
		File outputDir = new File(OUTPUT_DIRECTORY);

		String[] childs = outputDir.list();

		if (childs != null && childs.length > 0) {
			deleteDirectoryRecursively(outputDir);
			outputDir.mkdirs();
		}
	}

	private void deleteDirectoryRecursively(File directory) {
		File[] childs = directory.listFiles();
		if (childs != null) {
			for (File file : childs) {
				if (file.isDirectory()) {
					deleteDirectoryRecursively(file);
				} else {
					file.delete();
				}
			}
		}
		directory.delete();
	}

	private <T extends AnnotatedElement> void addCollection(List<File> files, Collection<T> compilationUnits) {
		if (compilationUnits == null) {
			return;
		}
		addCollection(files, compilationUnits.toArray(new Type[] {}));
	}

	private <T extends Type> void addCollection(List<File> files, T... compilationUnits) {
		if (compilationUnits == null) {
			return;
		}
		for (T element : compilationUnits) {
			addCollection(files, element);
		}
	}

	private <T extends Type> void addCollection(List<File> files, T element) {
		assert element != null;

		if (element instanceof Class<?>) {
			File file = toFile((Class<?>) element);
			if (file != null) {
				files.add(file);
			} else {
				// These are innerclasses, etc ... that should not be
				// defined in this way
			}
		} else if (element instanceof Package) {
			ClassFinder classFinder = new ClassFinder();
			addCollection(files, classFinder.findClassesInPackage(((Package) element).getName()));
		}
	}

	private String convertClassNameToResourcePath(String name) {
		return name.replace(".", File.separator);
	}

	public File toFile(Class<?> clazz) {
		File file = new File(TEST_SOURCE_FOLDER + File.separator + convertClassNameToResourcePath(clazz.getCanonicalName()) + SOURCE_FILE_SUFFIX);
		if (!file.exists()) {
			file = new File(MAIN_SOURCE_FOLDER + File.separator + convertClassNameToResourcePath(clazz.getCanonicalName()) + SOURCE_FILE_SUFFIX);
			if (!file.exists()) {
				return null;
			}
		}
		return file;
	}

	protected String getClassPath() {
		String classPath = System.getProperty("maven.test.class.path");
		if (classPath == null || classPath.length() == 0) {
			return System.getProperty("java.class.path");
		}

		classPath = classPath.replaceAll(", ", isWindows() ? ";" : ":").trim();
		return "\"" + classPath.substring(1, classPath.length() - 2).trim() + ";" + new File("target\\classes").getAbsolutePath() + "\"";
	}

	private String getOsName() {
		return System.getProperty("os.name");
	}

	private boolean isWindows() {
		return getOsName().startsWith("Windows");
	}

	private static String join(String[] array) {
		return Arrays.toString(array).replaceAll(",", "\n");
	}

}
