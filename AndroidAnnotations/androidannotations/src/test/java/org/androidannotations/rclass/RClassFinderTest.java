package org.androidannotations.rclass;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.manifest.SomeClass;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class RClassFinderTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addManifestProcessorParameter(RClassFinderTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void fails_if_cannot_find_R_class() {
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationErrorWithNoSource(result);
		assertCompilationErrorCount(1, result);
	}

}
