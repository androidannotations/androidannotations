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
package org.androidannotations.rest.spring;

import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
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
	public void clientWithBodyParameters() throws IOException {
		CompileResult result = compileFiles(ClientWithBodyParameters.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void clientWithWrongBodyParameters() throws IOException {
		CompileResult result = compileFiles(ClientWithWrongBodyParameters.class);
		assertCompilationErrorOn(ClientWithWrongBodyParameters.class, "@Get", result);
		assertCompilationErrorOn(ClientWithWrongBodyParameters.class, "@Head", result);
		assertCompilationErrorOn(ClientWithWrongBodyParameters.class, "@Options", result);
		assertCompilationErrorOn(ClientWithWrongBodyParameters.class, "@Post(\"/multipleBodyNotAcceptable/\")", result);
		assertCompilationErrorCount(7, result);
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
	public void clientWithWrongPathVariables() throws IOException {
		CompileResult result = compileFiles(ClientWithWrongPathVariables.class);
		assertCompilationErrorOn(ClientWithWrongPathVariables.class, "@Get(\"/duplicates/{v1}\")", result);
		assertCompilationErrorOn(ClientWithWrongPathVariables.class, "@Get(\"/missingvariable/{v1}\")", result);
		assertCompilationErrorOn(ClientWithWrongPathVariables.class, "@Path(\"v2\")", result);
		assertCompilationErrorOn(ClientWithWrongPathVariables.class, "@Path(\"missingGet\")", result);
		assertCompilationErrorCount(5, result);
	}

	@Test
	public void clientWithPostParameters() throws IOException {
		CompileResult result = compileFiles(ClientWithPostParameters.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void clientWithWrongPostParameters() throws IOException {
		CompileResult result = compileFiles(ClientWithWrongFields.class);
		assertCompilationErrorOn(ClientWithWrongFields.class, "void missingPostAnnotation(@Field(\"missingPost\") int v1);", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Field(\"missingPost\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Post(\"/pathParamAndEntity\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Path(\"conflict\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Post(\"/conflictElementNameWithPathParam\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Post(\"/conflictWithPathParamWithElementName\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Path(\"elementNameConflict\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Path(\"pathParamConflict\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Post(\"/conflictWithPathParam\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Post(\"/duplicateField\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Post(\"/fieldAndPartOnSameMethod\")", result);
		assertCompilationErrorOn(ClientWithWrongFields.class, "@Post(\"/fieldAndBodyOnSameMethod\")", result);

		assertCompilationErrorCount(12, result);
	}

	@Test
	public void clientWithParameters() throws IOException {
		CompileResult result = compileFiles(ClientWithParameters.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void fieldPathParamOnSameArgument() throws IOException {
		CompileResult result = compileFiles(FieldPathParamOnSameArgument.class);
		assertCompilationErrorOn(FieldPathParamOnSameArgument.class, "@Field", result);
		assertCompilationErrorOn(FieldPathParamOnSameArgument.class, "@Path", result);

		assertCompilationErrorCount(2, result);
	}

	@Test
	public void clientWithMissingFormConverter() throws IOException {
		CompileResult result = compileFiles(ClientWithMissingFormConverter.class);
		assertCompilationErrorCount(1, result);
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

	@Test
	public void patchWithoutSpring2DoesNotCompile() throws IOException {
		CompileResult result = compileFiles(ClientWithPatch.class);

		assertCompilationErrorOn(ClientWithPatch.class, "@Patch(\"/\")", result);
		assertCompilationErrorCount(1, result);
	}

	@Test
	public void clientWithWrongRequiresCookieInUrl() throws IOException {
		CompileResult result = compileFiles(ClientWithWrongRequiresCookieInUrl.class);
		assertCompilationErrorOn(ClientWithWrongRequiresCookieInUrl.class, "@Post(\"/badNamedRequiresCookieInUrl/?myCookieInUrl={myCookieInUrl}\")", result);
		assertCompilationErrorOn(ClientWithWrongRequiresCookieInUrl.class, "@Post(\"/noPlaceholderRequiresCookieInUrl\")", result);
	}
}
