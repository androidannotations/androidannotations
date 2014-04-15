package org.androidannotations.rest;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;

/**
 * Created by jmaltz on 4/16/14.
 */
public class RestHeadersTest extends AAProcessorTestHelper {

    @Before
    public void setup() {
        addManifestProcessorParameter(RestConverterTest.class);
        addProcessor(AndroidAnnotationProcessor.class);
    }
}
