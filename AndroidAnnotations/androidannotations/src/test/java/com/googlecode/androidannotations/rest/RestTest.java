package com.googlecode.androidannotations.rest;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.androidannotations.AndroidAnnotationProcessor;
import com.googlecode.androidannotations.utils.AAProcessorTestHelper;

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

}
