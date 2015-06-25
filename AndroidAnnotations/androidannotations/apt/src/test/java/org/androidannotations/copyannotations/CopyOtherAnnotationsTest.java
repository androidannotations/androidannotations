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
package org.androidannotations.copyannotations;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class CopyOtherAnnotationsTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(CopyOtherAnnotationsTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void testGeneratedClassWithCopiedAnnotationsCompiles() {
		assertCompilationSuccessful(compileFiles(HasOtherAnnotations.class));
	}

	@Test
	public void testGeneratedClassHasCopiedNonAAAnnotations() {
		// CHECKSTYLE:OFF
		String[] classHeader = { //
				"@XmlType", //
				"@TestTargetClass(String.class)", //
				"@WebServiceRefs({", //
				"    @WebServiceRef(type = String.class)", //
				"})", //
				"public final class HasOtherAnnotations_", };
		
		// CHECKSTYLE:ON
		compileFiles(HasOtherAnnotations.class);
		assertGeneratedClassContains(toGeneratedFile(HasOtherAnnotations.class), classHeader);
	}

	@Test
	public void testOverridenMethodHasCopiedNonAAAnnotations() {
		// CHECKSTYLE:OFF
		String[] methodSignature = { //
				"    @Addressing(responses = (javax.xml.ws.soap.AddressingFeature.Responses.ALL))", //
				"    @Action(input = \"someString\")", //
				"    @SuppressWarnings({", //
				"        \"\",", //
				"        \"hi\"", //
				"    })", //
				"    @Override", //
				"    public void onEvent(", //
				"        @Deprecated", //
				"        final Event event) {", };
		// CHECKSTYLE:ON
		
		compileFiles(HasOtherAnnotations.class);
		assertGeneratedClassContains(toGeneratedFile(HasOtherAnnotations.class), methodSignature);
	}

	@Test
	public void testOverrideDoesNotAddedTwice() {
		addProcessorParameter("trace", "true");
		compileFiles(HasOtherAnnotations.class);
		
		// CHECKSTYLE:OFF
		String[] methodSignature = { //
				"    @java.lang.Override", //
				"    @java.lang.Override", //
				"    public String toString() {", };
		// CHECKSTYLE:ON
		
		assertGeneratedClassDoesNotContain(toGeneratedFile(HasOtherAnnotations.class), methodSignature);
	}

	@Test
	public void testInheritedAnnotationsNotCopied() {
		compileFiles(HasOtherAnnotations.class);

		String[] annotation = { "@RunWith(Runner.class)", };

		assertGeneratedClassDoesNotContain(toGeneratedFile(HasOtherAnnotations.class), annotation);
	}
}
