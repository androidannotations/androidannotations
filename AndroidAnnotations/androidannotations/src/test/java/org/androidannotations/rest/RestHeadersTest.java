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
