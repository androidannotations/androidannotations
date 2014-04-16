package org.androidannotations.rest;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jmaltz on 4/16/14.
 */
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
}
