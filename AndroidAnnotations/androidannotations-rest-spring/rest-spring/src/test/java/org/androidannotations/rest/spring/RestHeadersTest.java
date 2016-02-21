/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.rest.spring;

import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.androidannotations.testutils.ProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class RestHeadersTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(RestConverterTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void clientWithOneHeaderCompiles() {
		ProcessorTestHelper.CompileResult result = compileFiles(ClientWithOneHeader.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void clientWithOneHeaderInMultipleAnnotationCompiles() {
		CompileResult result = compileFiles(ClientWithOneHeaderInHeaders.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void clientWithMultipleHeadersCompiles() {
		CompileResult result = compileFiles(ClientWithMultipleHeaders.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void clientWithSingleAndMultipleHeadersOnSameMethod() throws IOException {
		CompileResult result = compileFiles(ClientWithHeaderAndHeadersOnSameMethod.class);

		assertCompilationErrorOn(ClientWithHeaderAndHeadersOnSameMethod.class, "@Header(name = \"testKey1\", value = \"testVal1\")", result);
		assertCompilationErrorOn(ClientWithHeaderAndHeadersOnSameMethod.class, "@Headers(@Header(name = \"testKey\", value = \"testVal\"))", result);
		assertCompilationErrorCount(2, result);
	}

	@Test
	public void clientWithHeaderOnWrongMethod() throws IOException {
		CompileResult result = compileFiles(ClientWithHeaderOnWrongMethod.class);

		assertCompilationErrorOn(ClientWithHeaderOnWrongMethod.class, "@Header(name = \"testKey1\", value = \"testVal1\")", result);
		assertCompilationErrorCount(1, result);
	}

}
