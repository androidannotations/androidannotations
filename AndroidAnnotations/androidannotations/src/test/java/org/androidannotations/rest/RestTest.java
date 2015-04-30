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
package org.androidannotations.rest;

import java.io.IOException;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class RestTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(RestTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void classClientDoesNotCompile() throws IOException {
		CompileResult result = compileFiles(ClassClient.class);
		assertCompilationErrorOn(ClassClient.class, "@Rest", result);
	}

	@Test
	public void clientNoInternetPermissionDoesNotCompile() throws IOException {
		addManifestProcessorParameter(RestTest.class, "NoInternetPermissionManifest.xml");
		CompileResult result = compileFiles(ClientWithNoConverters.class);
		assertCompilationErrorOn(ClientWithNoConverters.class, "@Rest", result);
	}

	@Test
	public void clientWithReturnType() throws IOException {
		CompileResult result = compileFiles(ClientWithResponseEntity.class);
		assertCompilationErrorOn(ClientWithResponseEntity.class, "@Options", result);
		assertCompilationErrorOn(ClientWithResponseEntity.class, "@Head", result);
		assertCompilationErrorCount(2, result);
	}

	@Test
	public void clientWithRequestEntity() throws IOException {
		CompileResult result = compileFiles(ClientWithRequestEntity.class);
		assertCompilationErrorOn(ClientWithRequestEntity.class, "@Get", result);
		assertCompilationErrorOn(ClientWithRequestEntity.class, "@Head", result);
		assertCompilationErrorOn(ClientWithRequestEntity.class, "@Options", result);
		assertCompilationErrorCount(3, result);
	}

	@Test
	public void clientWithPrimitiveReturnTypes() throws IOException {
		CompileResult result = compileFiles(ClientWithPrimitiveReturnType.class);
		assertCompilationErrorOn(ClientWithPrimitiveReturnType.class, "@Delete", result);
		assertCompilationErrorOn(ClientWithPrimitiveReturnType.class, "@Get", result);
		assertCompilationErrorOn(ClientWithPrimitiveReturnType.class, "@Head", result);
		assertCompilationErrorOn(ClientWithPrimitiveReturnType.class, "@Options", result);
		assertCompilationErrorOn(ClientWithPrimitiveReturnType.class, "@Post", result);
		assertCompilationErrorOn(ClientWithPrimitiveReturnType.class, "@Put", result);
		assertCompilationErrorCount(6, result);
	}

	@Test
	public void clientWithPathVariables() throws IOException {
		CompileResult result = compileFiles(ClientWithPathVariable.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void clientWithWrongEnhancedMethods() throws IOException {
		CompileResult result = compileFiles(ClientWithWrongEnhancedMethod.class);
		assertCompilationErrorOn(ClientWithWrongEnhancedMethod.class, "Object getRestTemplate();", result);
		assertCompilationErrorOn(ClientWithWrongEnhancedMethod.class, "String getURL();", result);
		assertCompilationErrorOn(ClientWithWrongEnhancedMethod.class, "String getRootURL();", result);
		assertCompilationErrorOn(ClientWithWrongEnhancedMethod.class, "String getRootURL(String param);", result);
		assertCompilationErrorOn(ClientWithWrongEnhancedMethod.class, "boolean setRootURL();", result);
		assertCompilationErrorCount(5, result);
	}

	@Test
	public void clientWithWrongInterface() throws IOException {
		CompileResult result = compileFiles(ClientWithWrongInterface.class);
		assertCompilationErrorCount(1, result);
	}

	@Test
	public void clientWithAllInterfaces() throws IOException {
		CompileResult result = compileFiles(ClientWithAllInterfaces.class);
		assertCompilationSuccessful(result);
	}
}
