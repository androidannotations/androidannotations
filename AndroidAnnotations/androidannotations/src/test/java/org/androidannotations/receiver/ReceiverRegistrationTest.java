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
package org.androidannotations.receiver;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ReceiverRegistrationTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addManifestProcessorParameter(ActivityWithValidReceiver.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activity_with_valid_receiver_annotation_compile() throws IOException {
		CompileResult result = compileFiles(ActivityWithValidReceiver.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void activity_with_invalid_registerAt_not_compile() throws IOException {
		CompileResult result = compileFiles(ActivityWithInvalidRegisterAt.class);
		assertCompilationErrorOn(ActivityWithInvalidRegisterAt.class, "@Receiver", result);
	}

	@Test
	public void activity_with_two_method_with_same_name_not_compile() throws IOException {
		CompileResult result = compileFiles(ActivityWithTwoSameNameMethod.class);
		assertCompilationErrorOn(ActivityWithTwoSameNameMethod.class, "@Receiver", result);
		assertCompilationErrorCount(2, result);
	}

	@Test
	public void fragment_with_valid_receiver_annotation_compile() throws IOException {
		CompileResult result = compileFiles(FragmentWithValidReceiver.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void service_with_valid_receiver_annotation_compile() throws IOException {
		CompileResult result = compileFiles(ServiceWithValidReceiver.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void service_with_invalid_registerAt_not_compile() throws IOException {
		CompileResult result = compileFiles(ServiceWithInvalidReceiver.class);
		assertCompilationErrorOn(ServiceWithInvalidReceiver.class, "@Receiver", result);
		assertCompilationErrorCount(3, result);
	}

}
