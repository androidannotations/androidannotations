/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package org.androidannotations.rest;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;

public class RestConverterTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addManifestProcessorParameter(RestConverterTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void client_with_no_converters_compiles() {
		CompileResult result = compileFiles(ClientWithNoConverters.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void client_with_valid_converter_compiles() {
		CompileResult result = compileFiles(ClientWithValidConverter.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void client_with_abstract_converter_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ClientWithAbstractConverter.class);
		assertCompilationErrorOn(ClientWithAbstractConverter.class, "@Rest", result);
	}

	@Test
	public void client_with_non_converter_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ClientWithNonConverter.class);
		assertCompilationErrorOn(ClientWithNonConverter.class, "@Rest", result);
	}

	@Test
	public void client_with_wrong_constructor_converter_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ClientWithWrongConstructorConverter.class);
		assertCompilationErrorOn(ClientWithWrongConstructorConverter.class, "@Rest", result);
	}

}
