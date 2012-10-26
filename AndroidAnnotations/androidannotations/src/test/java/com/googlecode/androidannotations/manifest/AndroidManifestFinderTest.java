package com.googlecode.androidannotations.manifest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.googlecode.androidannotations.AndroidAnnotationProcessor;
import com.googlecode.androidannotations.utils.AAProcessorTestHelper;

public class AndroidManifestFinderTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addProcessor(new AndroidAnnotationProcessor());
	}

	@Test
	public void fails_if_no_manifest() throws Exception {
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationError(result);
	}

	@Test
	public void finds_specified_manifest() {
		addManifestProcessorParameter(AndroidManifestFinderTest.class);
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void finds_manifest_in_generated_source_parent_folder() throws Exception {
		copyManifestToParentOfOutputDirectory();
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationSuccessful(result);
		deleteManifestFromParentOfOutputDirectory();
	}

	private void deleteManifestFromParentOfOutputDirectory() {
		manifestFileInParentOfOutputDirectory().delete();
	}

	private void copyManifestToParentOfOutputDirectory() throws FileNotFoundException, IOException {
		InputSupplier<InputStream> from = new InputSupplier<InputStream>() {
			@Override
			public InputStream getInput() throws IOException {
				return AndroidManifestFinderTest.class.getResourceAsStream("AndroidManifest.xml");
			}
		};
		Files.copy(from, manifestFileInParentOfOutputDirectory());
	}

	private File manifestFileInParentOfOutputDirectory() {
		File outputDirectory = getOuputDirectory();
		File manifestFile = new File(outputDirectory.getParentFile(), "AndroidManifest.xml");
		return manifestFile;
	}

}
