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
package org.androidannotations.rest;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

import dalvik.annotation.TestTargetClass;

public class RestHeadersTest extends AAProcessorTestHelper {

    @Before
    public void setup() {
        addManifestProcessorParameter(RestConverterTest.class);
        addProcessor(AndroidAnnotationProcessor.class);
    }

    @Test
    public void client_with_one_header_compiles() {
        CompileResult result = compileFiles(ClientWithOneHeader.class);
        assertCompilationSuccessful(result);
    }

    @Test
    public void client_with_one_header_in_multiple_annotation_compiles() {
        CompileResult result = compileFiles(ClientWithOneHeaderInHeaders.class);
        assertCompilationSuccessful(result);
    }

    @Test
    public void client_with_multiple_headers_test() {
        CompileResult result = compileFiles(ClientWithMultipleHeaders.class);
    }
}
