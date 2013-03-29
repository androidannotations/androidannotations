/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
	public void setup() {
		addManifestProcessorParameter(RestTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void class_client_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ClassClient.class);
		assertCompilationErrorOn(ClassClient.class, "@Rest", result);
	}

	@Test
	public void client_no_internet_permission_does_not_compile() throws IOException {
		addManifestProcessorParameter(RestTest.class, "NoInternetPermissionManifest.xml");
		CompileResult result = compileFiles(ClientWithNoConverters.class);
		assertCompilationErrorOn(ClientWithNoConverters.class, "@Rest", result);
	}

	@Test
	public void client_with_return_type() throws IOException {
		CompileResult result = compileFiles(ClientWithResponseEntity.class);
		assertCompilationErrorOn(ClientWithResponseEntity.class, "@Put", result);
		assertCompilationErrorOn(ClientWithResponseEntity.class, "@Delete", result);
		assertCompilationErrorOn(ClientWithResponseEntity.class, "@Options", result);
		assertCompilationErrorOn(ClientWithResponseEntity.class, "@Head", result);
		assertCompilationErrorCount(4, result);
	}

	@Test
	public void client_with_request_entity() throws IOException {
		CompileResult result = compileFiles(ClientWithRequestEntity.class);
		assertCompilationErrorOn(ClientWithRequestEntity.class, "@Delete", result);
		assertCompilationErrorOn(ClientWithRequestEntity.class, "@Get", result);
		assertCompilationErrorOn(ClientWithRequestEntity.class, "@Head", result);
		assertCompilationErrorOn(ClientWithRequestEntity.class, "@Options", result);
		assertCompilationErrorCount(4, result);
	}

	@Test
	public void client_with_primitive_return_types() throws IOException {
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
	public void client_with_path_variables() throws IOException {
		CompileResult result = compileFiles(ClientWithPathVariable.class);
		assertCompilationSuccessful(result);
	}

}
