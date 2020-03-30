/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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

import java.io.File;
import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class GenerateJavaDocTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(ActivityInManifest.class);
		addProcessor(AndroidAnnotationProcessor.class);
		ensureOutputDirectoryIsEmpty();
	}

	@Test
	public void generateJavaDocForActivityExtra() throws IOException {
		CompileResult result = compileFiles(ActivityWithExtras.class);
		File generatedFile = toGeneratedFile(ActivityWithExtras.class);

		assertCompilationSuccessful(result);

		assertGeneratedClassMatches(generatedFile, ".*\\* this is a javadoc comment");
		assertGeneratedClassMatches(generatedFile, ".*\\* @param testExtra");
		assertGeneratedClassMatches(generatedFile, ".*\\* @return");
	}

	@Test
	public void generateJavaDocForFragementArg() throws IOException {
		CompileResult result = compileFiles(FragmentWithArg.class);
		File generatedFile = toGeneratedFile(FragmentWithArg.class);

		assertCompilationSuccessful(result);

		assertGeneratedClassMatches(generatedFile, ".*\\* this is a javadoc comment");
		assertGeneratedClassMatches(generatedFile, ".*\\* @param testArg");
		assertGeneratedClassMatches(generatedFile, ".*\\* @return");
	}

	@Test
	public void generateJavaDocForServiceAction() throws IOException {
		CompileResult result = compileFiles(ServiceWithServiceAction.class);
		File generatedFile = toGeneratedFile(ServiceWithServiceAction.class);

		assertCompilationSuccessful(result);
		// CHECKSTYLE:OFF
		String[] doc = new String[] { //
				"         * this is a javadoc comment", //
				"         * ", //
				"         *  @param param", //
				"         *             this is a param", //
				"         * ", //
				"         * @return", //
				"         *     the IntentBuilder to chain calls" };
		// CHECKSTYLE:ON

		assertGeneratedClassContains(generatedFile, doc);
	}

	@Test
	public void generateJavaDocForSharedPref() throws IOException {
		CompileResult result = compileFiles(SharedPrefWithJavaDoc.class);
		File generatedFile = toGeneratedFile(SharedPrefWithJavaDoc.class);

		assertCompilationSuccessful(result);

		// CHECKSTYLE:OFF
		String[] fieldDoc = { //
				"     * <p><b>Defaults to</b>: 42</p>", //
				"     * The Age!", //
				"     *  ", //
				"     *  @deprecated use {@link #ageLong()}", //
				"     * ", //
				"     * @return", //
				"     *     a {@link IntPrefField} instance to retrieve or write the pref value", //
				"     */", //
				"    public IntPrefField age() {", //
		};
		String[] editorDoc = { //
				"         * The Age!", //
				"         *  ", //
				"         *  @deprecated use {@link #ageLong()}", //
				"         */", //
				"        public IntPrefEditorField<SharedPrefWithJavaDoc_.SharedPrefWithJavaDocEditor_> age() {", //
		};
		// CHECKSTYLE:ON
		assertGeneratedClassContains(generatedFile, fieldDoc);
		assertGeneratedClassContains(generatedFile, editorDoc);
	}

	@Test
	public void generateJavaDocForEmptySharedPrefStringFields() throws IOException {
		CompileResult result = compileFiles(SharedPrefWithJavaDoc.class);
		File generatedFile = toGeneratedFile(SharedPrefWithJavaDoc.class);

		assertCompilationSuccessful(result);

		// CHECKSTYLE:OFF
		String[] fieldDoc = { //
				"     * <p><b>Defaults to</b>: \"\"</p>", //
				"     * ", //
				"     * ", //
				"     * @return", //
				"     *     a {@link StringPrefField} instance to retrieve or write the pref value", //
				"     */", //
				"    public StringPrefField title() {", //
		};
		// CHECKSTYLE:ON
		assertGeneratedClassContains(generatedFile, fieldDoc);
	}

	@Test
	public void generateJavaDocForNonEmptySharedPrefStringFields() throws IOException {
		CompileResult result = compileFiles(SharedPrefWithJavaDoc.class);
		File generatedFile = toGeneratedFile(SharedPrefWithJavaDoc.class);

		assertCompilationSuccessful(result);

		// CHECKSTYLE:OFF
		String[] fieldDoc = { //
				"     * <p><b>Defaults to</b>: \"something\"</p>", //
				"     * ", //
				"     * ", //
				"     * @return", //
				"     *     a {@link StringPrefField} instance to retrieve or write the pref value", //
				"     */", //
				"    public StringPrefField something() {", //
		};
		// CHECKSTYLE:ON
		assertGeneratedClassContains(generatedFile, fieldDoc);
	}

}
