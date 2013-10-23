package org.androidannotations.generation;

import java.io.File;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class ActivityIntentFragmentTest extends AAProcessorTestHelper {

	private static final String INTENT_FRAGMENT_SIGNATURE = "public static ActivityInManifest_.IntentBuilder_ intent(Fragment fragment)";

	@Before
	public void setup() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activity_intent_minSdkFroyo_compileWithFroyo() {
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinFroyo.xml");
		CompileResult result = compileFiles(ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassDoesntContains(generatedFile, INTENT_FRAGMENT_SIGNATURE);
	}

	@Test
	public void activity_intent_minSdkFroyo_compileWithJB() {
		// To simulate comilation with SDK > 11, we add Fragment on classpath
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinFroyo.xml");
		CompileResult result = compileFiles(toPath(ActivityIntentFragmentTest.class, "Fragment.java"), ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassDoesntContains(generatedFile, INTENT_FRAGMENT_SIGNATURE);
	}

	@Test
	public void activity_intent_minSdkJB_compileWithJB() {
		// To simulate comilation with SDK > 11, we add Fragment on classpath
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinJB.xml");
		CompileResult result = compileFiles(toPath(ActivityIntentFragmentTest.class, "Fragment.java"), ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassContains(generatedFile, INTENT_FRAGMENT_SIGNATURE);
	}

}
