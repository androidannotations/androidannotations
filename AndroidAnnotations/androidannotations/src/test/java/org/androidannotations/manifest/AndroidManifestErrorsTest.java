package org.androidannotations.manifest;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;

public class AndroidManifestErrorsTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addManifestProcessorParameter(AndroidManifestErrorsTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activity_subclass_in_manifest_compiles() {
		CompileResult result = compileFiles(ActivitySubclassInManifest.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void activity_in_manifest_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ActivityInManifest.class);
		assertCompilationErrorOn(ActivityInManifest.class, "@EActivity", result);
	}

	@Test
	public void activity_not_in_manifest_compiles_with_warning() throws IOException {
		CompileResult result = compileFiles(ActivityNotInManifest.class);
		assertCompilationWarningOn(ActivityNotInManifest.class, "@EActivity", result);
	}
}
