/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
	public void setup() {
		addManifestProcessorParameter(CopyOtherAnnotationsTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void testGeneretedClassWithCopiedAnnotationsCompiles() {
		assertCompilationSuccessful(compileFiles(HasOtherAnnotations.class));
	}

	@Test
	public void testGeneratedClassHasCopiedNonAAAnnotations() {
		String[] classHeader = { //
				"@XmlType", //
				"@TestTargetClass(String.class)", //
				"@WebServiceRefs({", //
				"    @WebServiceRef(type = String.class)", //
				"})", //
				"public final class HasOtherAnnotations_" };

		compileFiles(HasOtherAnnotations.class);
		assertGeneratedClassContains(toGeneratedFile(HasOtherAnnotations.class), classHeader);
	}

	@Test
	public void testOverridenMethodHasCopiedNonAAAnnotations() {
		String[] methodSignature = { //
				"    @Addressing(responses = (javax.xml.ws.soap.AddressingFeature.Responses.ALL))", //
				"    @Action(input = \"someString\")", //
				"    @SuppressWarnings({", //
				"        \"\",", //
				"        \"hi\"", //
				"    })", //
				"    @Override", //
				"    public void onEvent(final Event event) {" };

		compileFiles(HasOtherAnnotations.class);
		assertGeneratedClassContains(toGeneratedFile(HasOtherAnnotations.class), methodSignature);
	}

	@Test
	public void testOverrideDoesNotAddedTwice() {
		addProcessorParameter("trace", "true");
		compileFiles(HasOtherAnnotations.class);

		String[] methodSignature = { //
				"    @java.lang.Override", //
				"    @java.lang.Override", //
				"    public String toString() {" };

		assertGeneratedClassDoesNotContain(toGeneratedFile(HasOtherAnnotations.class), methodSignature);
	}
}
