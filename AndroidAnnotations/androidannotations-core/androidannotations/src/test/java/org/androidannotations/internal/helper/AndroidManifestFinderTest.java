/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.internal.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

@RunWith(Parameterized.class)
public class AndroidManifestFinderTest {

	private static final String GRADLE_GEN_FOLDER = "build/generated/source/apt/debug";
	private static final String GRADLE_FLAVOR_GEN_FOLDER = "build/generated/source/apt/flavor/debug";
	private static final String GRADLE_KOTLIN_GEN_FOLDER = "build/generated/source/kapt/debug";
	private static final String GRADLE_KOTLIN_FLAVOR_GEN_FOLDER = "build/generated/source/kapt/flavorDebug";

	private static final String MAVEN_GEN_FOLDER = "target/generated-sources/annotations";

	private static final String ECLIPSE_GEN_FOLDER = ".apt_generated";

	private final String genFolderPath;
	private final String manifestFolderPath;
	private final boolean shouldFind;

	private Path tempDirectory;

	public AndroidManifestFinderTest(String genFolderPath, String manifestFolderPath, boolean shouldFind) {
		this.genFolderPath = genFolderPath;
		this.manifestFolderPath = manifestFolderPath;
		this.shouldFind = shouldFind;
	}

	@Parameterized.Parameters(name = "genFolderPath = {0}, manifestFolderPath = {1}, shouldFind = {2}")
	public static Iterable<Object[]> createTestData() {

		// CHECKSTYLE:OFF
		Object[] gradleManifestFoundInManifests = { GRADLE_GEN_FOLDER, "build/intermediates/manifests/full/debug", true };
		Object[] gradleManifestFoundInBundles = { GRADLE_GEN_FOLDER, "build/intermediates/bundles/debug", true };
		Object[] gradleManifestFoundInManifestsAapt = { GRADLE_GEN_FOLDER, "build/intermediates/manifests/aapt/debug", true };
		Object[] gradleManifestFoundInManifestsWithFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/full/flavor/debug", true };
		Object[] gradleManifestFoundInBundlesWithFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/bundles/flavor/debug", true };
		Object[] gradleManifestFoundInManifestsAaptWithFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/aapt/flavor/debug", true };
		Object[] gradleManifestFoundInManifestsWithAbiSplit = { GRADLE_GEN_FOLDER, "build/intermediates/manifests/full/debug/x86", true };
		Object[] gradleManifestFoundInManifestsWithAbiSplitAndFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/full/flavor/debug/x86", true };
		Object[] gradleManifestFoundInManifestsWithDensitySplit = { GRADLE_GEN_FOLDER, "build/intermediates/manifests/full/debug/hdpi", true };
		Object[] gradleManifestFoundInManifestsWithDensitySplitAndFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/full/flavor/debug/hdpi", true };
		Object[] gradleManifestFoundInManifestsWithBothSplit = { GRADLE_GEN_FOLDER, "build/intermediates/manifests/full/debug/x86/hdpi", true };
		Object[] gradleManifestFoundInManifestsWithBothSplitAndFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/full/flavor/debug/x86/hdpi", true };
		Object[] gradleManifestFoundInMergedManifests = { GRADLE_GEN_FOLDER, "build/intermediates/merged_manifests/debug/processDebugManifest/merged", true };
		Object[] gradleManifestFoundInMergedManifestsWithAbiSplit = { GRADLE_GEN_FOLDER, "build/intermediates/merged_manifests/debug/processDebugManifest/merged/x86", true };
		Object[] gradleManifestFoundInMergedManifestsWithAbiSplitAndFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/processFlavorDebugManifest/merged/x86",
				true };
		Object[] gradleManifestFoundInMergedManifestsWithDensitySplit = { GRADLE_GEN_FOLDER, "build/intermediates/merged_manifests/debug/processDebugManifest/merged/hdpi", true };
		Object[] gradleManifestFoundInMergedManifestsWithDensitySplitAndFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/processFlavorDebugManifest/merged/hdpi",
				true };
		Object[] gradleManifestFoundInMergedManifestsWithBothSplit = { GRADLE_GEN_FOLDER, "build/intermediates/merged_manifests/debug/processDebugManifest/merged/x86/hdpi", true };
		Object[] gradleManifestFoundInMergedManifestsWithBothSplitAndFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/processFlavorDebugManifest/merged/x86/hdpi",
				true };
		Object[] gradleManifestFoundInMergedManifestsV33 = { GRADLE_GEN_FOLDER, "build/intermediates/merged_manifests/debug/", true };
		Object[] gradleManifestFoundInMergedManifestsWithAbiSplitV33 = { GRADLE_GEN_FOLDER, "build/intermediates/merged_manifests/debug/x86", true };
		Object[] gradleManifestFoundInMergedManifestsWithAbiSplitAndFlavorV33 = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/x86", true };
		Object[] gradleManifestFoundInMergedManifestsWithDensitySplitV33 = { GRADLE_GEN_FOLDER, "build/intermediates/merged_manifests/debug/hdpi", true };
		Object[] gradleManifestFoundInMergedManifestsWithDensitySplitAndFlavorV33 = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/hdpi", true };
		Object[] gradleManifestFoundInMergedManifestsWithBothSplitV33 = { GRADLE_GEN_FOLDER, "build/intermediates/merged_manifests/debug/x86/hdpi", true };
		Object[] gradleManifestFoundInMergedManifestsWithBothSplitAndFlavorV33 = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/x86/hdpi", true };

		Object[] gradleManifestFoundInMergedManifestsV35 = { "build/generated/ap_generated_sources/debug/out", "build/intermediates/merged_manifests/debug/", true };
		Object[] gradleManifestFoundInMergedManifestsWithAbiSplitV35 = { "build/generated/ap_generated_sources/debug/out", "build/intermediates/merged_manifests/debug/x86", true };
		Object[] gradleManifestFoundInMergedManifestsWithAbiSplitAndFlavorV35 = { "build/generated/ap_generated_sources/flavorDebug/out", "build/intermediates/merged_manifests/flavorDebug/x86",
				true };
		Object[] gradleManifestFoundInMergedManifestsWithDensitySplitV35 = { "build/generated/ap_generated_sources/debug/out", "build/intermediates/merged_manifests/debug/hdpi", true };
		Object[] gradleManifestFoundInMergedManifestsWithDensitySplitAndFlavorV35 = { "build/generated/ap_generated_sources/flavorDebug/out", "build/intermediates/merged_manifests/flavorDebug/hdpi",
				true };
		Object[] gradleManifestFoundInMergedManifestsWithBothSplitV35 = { "build/generated/ap_generated_sources/debug/out", "build/intermediates/merged_manifests/debug/x86/hdpi", true };
		Object[] gradleManifestFoundInMergedManifestsWithBothSplitAndFlavorV35 = { "build/generated/ap_generated_sources/flavorDebug/out", "build/intermediates/merged_manifests/flavorDebug/x86/hdpi",
				true };

		Object[] gradleKotlinManifestFoundInManifests = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/manifests/full/debug", true };
		Object[] gradleKotlinManifestFoundInBundles = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/bundles/debug", true };
		Object[] gradleKotlinManifestFoundInManifestsAapt = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/manifests/aapt/debug", true };
		Object[] gradleKotlinManifestFoundInManifestsLibrary = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/library_manifest/debug", true };
		Object[] gradleKotlinManifestFoundInManifestsWithFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/full/flavor/debug", true };
		Object[] gradleKotlinManifestFoundInBundlesWithFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/bundles/flavor/debug", true };
		Object[] gradleKotlinManifestFoundInManifestsAaptWithFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/aapt/flavor/debug", true };
		Object[] gradleKotlinManifestFoundInManifestsLibraryWithFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/library_manifest/flavorDebug", true };
		Object[] gradleKotlinManifestFoundInManifestsWithAbiSplit = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/manifests/full/debug/x86", true };
		Object[] gradleKotlinManifestFoundInManifestsWithAbiSplitAndFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/full/flavor/debug/x86", true };
		Object[] gradleKotlinManifestFoundInManifestsWithDensitySplit = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/manifests/full/debug/hdpi", true };
		Object[] gradleKotlinManifestFoundInManifestsWithDensitySplitAndFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/full/flavor/debug/hdpi", true };
		Object[] gradleKotlinManifestFoundInManifestsWithBothSplit = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/manifests/full/debug/x86/hdpi", true };
		Object[] gradleKotlinManifestFoundInManifestsWithBothSplitAndFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/manifests/full/flavor/debug/x86/hdpi", true };
		Object[] gradleKotlinManifestFoundInMergedManifests = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/merged_manifests/debug/processDebugManifest/merged", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithAbiSplit = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/merged_manifests/debug/processDebugManifest/merged/x86", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithAbiSplitAndFlavor = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/processFlavorDebugManifest/merged/x86",
				true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithDensitySplit = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/merged_manifests/debug/processDebugManifest/merged/hdpi", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithDensitySplitAndFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER,
				"build/intermediates/merged_manifests/flavorDebug/processFlavorDebugManifest/merged/hdpi", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithBothSplit = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/merged_manifests/debug/processDebugManifest/merged/x86/hdpi", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithBothSplitAndFlavor = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER,
				"build/intermediates/merged_manifests/flavorDebug/processFlavorDebugManifest/merged/x86/hdpi", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsV33 = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/merged_manifests/debug/", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithAbiSplitV33 = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/merged_manifests/debug/x86", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithAbiSplitAndFlavorV33 = { GRADLE_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/x86", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithDensitySplitV33 = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/merged_manifests/debug/hdpi", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithDensitySplitAndFlavorV33 = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/hdpi", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithBothSplitV33 = { GRADLE_KOTLIN_GEN_FOLDER, "build/intermediates/merged_manifests/debug/x86/hdpi", true };
		Object[] gradleKotlinManifestFoundInMergedManifestsWithBothSplitAndFlavorV33 = { GRADLE_KOTLIN_FLAVOR_GEN_FOLDER, "build/intermediates/merged_manifests/flavorDebug/x86/hdpi", true };
		// CHECKSTYLE:ON

		Object[] mavenManifestFoundInTarget = { MAVEN_GEN_FOLDER, "target", true };
		Object[] mavenManifestFoundInSrc = { MAVEN_GEN_FOLDER, "src/main", true };
		Object[] mavenManifestFoundInRoot = { MAVEN_GEN_FOLDER, "", true };

		Object[] eclipseManifestFound = { ECLIPSE_GEN_FOLDER, "", true };

		Object[] gradleManifestNotFound = { GRADLE_GEN_FOLDER, "", false };

		Object[] gradleKotlinManifestNotFound = { GRADLE_KOTLIN_GEN_FOLDER, "", false };

		Object[] mavenManifestNotFound = { MAVEN_GEN_FOLDER, "something", false };

		Object[] eclipseManifestNotFound = { ECLIPSE_GEN_FOLDER, "something", false };

		Object[] noGeneratedFolderFound = { "", "", false };

		return Arrays.asList(gradleManifestFoundInManifests, gradleManifestFoundInBundles, gradleManifestFoundInManifestsAapt, gradleManifestFoundInManifestsWithFlavor,
				gradleManifestFoundInBundlesWithFlavor, gradleManifestFoundInManifestsAaptWithFlavor, gradleManifestFoundInManifestsWithAbiSplit, gradleManifestFoundInManifestsWithAbiSplitAndFlavor,
				gradleManifestFoundInManifestsWithDensitySplit, gradleManifestFoundInManifestsWithDensitySplitAndFlavor, gradleManifestFoundInManifestsWithBothSplit,
				gradleManifestFoundInManifestsWithBothSplitAndFlavor, gradleManifestFoundInMergedManifests, gradleManifestFoundInMergedManifestsWithAbiSplit,
				gradleManifestFoundInMergedManifestsWithAbiSplitAndFlavor, gradleManifestFoundInMergedManifestsWithDensitySplit, gradleManifestFoundInMergedManifestsWithDensitySplitAndFlavor,
				gradleManifestFoundInMergedManifestsWithBothSplit, gradleManifestFoundInMergedManifestsWithBothSplitAndFlavor, gradleManifestFoundInMergedManifestsV33,
				gradleManifestFoundInMergedManifestsWithAbiSplitV33, gradleManifestFoundInMergedManifestsWithAbiSplitAndFlavorV33, gradleManifestFoundInMergedManifestsWithDensitySplitV33,
				gradleManifestFoundInMergedManifestsWithDensitySplitAndFlavorV33, gradleManifestFoundInMergedManifestsWithBothSplitV33, gradleManifestFoundInMergedManifestsWithBothSplitAndFlavorV33,
				gradleManifestFoundInMergedManifestsV35, gradleManifestFoundInMergedManifestsWithAbiSplitV35, gradleManifestFoundInMergedManifestsWithAbiSplitAndFlavorV35,
				gradleManifestFoundInMergedManifestsWithDensitySplitV35, gradleManifestFoundInMergedManifestsWithDensitySplitAndFlavorV35, gradleManifestFoundInMergedManifestsWithBothSplitV35,
				gradleManifestFoundInMergedManifestsWithBothSplitAndFlavorV35, gradleKotlinManifestFoundInManifests, gradleKotlinManifestFoundInBundles, gradleKotlinManifestFoundInManifestsAapt,
				gradleKotlinManifestFoundInManifestsLibrary, gradleKotlinManifestFoundInManifestsWithFlavor, gradleKotlinManifestFoundInBundlesWithFlavor,
				gradleKotlinManifestFoundInManifestsAaptWithFlavor, gradleKotlinManifestFoundInManifestsLibraryWithFlavor, gradleKotlinManifestFoundInManifestsWithAbiSplit,
				gradleKotlinManifestFoundInManifestsWithAbiSplitAndFlavor, gradleKotlinManifestFoundInManifestsWithDensitySplit, gradleKotlinManifestFoundInManifestsWithDensitySplitAndFlavor,
				gradleKotlinManifestFoundInManifestsWithBothSplit, gradleKotlinManifestFoundInManifestsWithBothSplitAndFlavor, gradleKotlinManifestFoundInMergedManifests,
				gradleKotlinManifestFoundInMergedManifestsWithAbiSplit, gradleKotlinManifestFoundInMergedManifestsWithAbiSplitAndFlavor, gradleKotlinManifestFoundInMergedManifestsWithDensitySplit,
				gradleKotlinManifestFoundInMergedManifestsWithDensitySplitAndFlavor, gradleKotlinManifestFoundInMergedManifestsWithBothSplit,
				gradleKotlinManifestFoundInMergedManifestsWithBothSplitAndFlavor, gradleKotlinManifestFoundInMergedManifestsV33, gradleKotlinManifestFoundInMergedManifestsWithAbiSplitV33,
				gradleKotlinManifestFoundInMergedManifestsWithAbiSplitAndFlavorV33, gradleKotlinManifestFoundInMergedManifestsWithDensitySplitV33,
				gradleKotlinManifestFoundInMergedManifestsWithDensitySplitAndFlavorV33, gradleKotlinManifestFoundInMergedManifestsWithBothSplitV33,
				gradleKotlinManifestFoundInMergedManifestsWithBothSplitAndFlavorV33, mavenManifestFoundInTarget, mavenManifestFoundInSrc, mavenManifestFoundInRoot, eclipseManifestFound,
				gradleManifestNotFound, gradleKotlinManifestNotFound, mavenManifestNotFound, eclipseManifestNotFound, noGeneratedFolderFound);
	}

	@Test
	public void testFindManifestInKnownPathsStartingFromGenFolder() throws IOException {
		AndroidAnnotationsEnvironment mockEnvironment = Mockito.mock(AndroidAnnotationsEnvironment.class);
		Mockito.when(mockEnvironment.getOptionBooleanValue(AndroidManifestFinder.OPTION_INSTANT_FEATURE)).thenReturn(false);

		AndroidManifestFinder finder = new AndroidManifestFinder(mockEnvironment);
		tempDirectory = Files.createTempDirectory("AA");

		File genFolder = createGenFolder(genFolderPath);

		File manifestFolder = new File(tempDirectory.toString(), manifestFolderPath);
		createFolder(manifestFolder);

		File expectedManifest = new File(manifestFolder, "AndroidManifest.xml");
		createFile(expectedManifest);

		File foundManifest = finder.findManifestInKnownPathsStartingFromGenFolder(genFolder.getAbsolutePath());

		assertEquals(shouldFind, Objects.equals(expectedManifest, foundManifest));
	}

	private File createGenFolder(String genFolder) {
		File genFolderFile = new File(tempDirectory.toString(), genFolder);
		createFolder(genFolderFile);

		return genFolderFile;
	}

	private void createFolder(File folder) {
		if (folder.exists()) {
			return;
		}

		boolean folderCreated = folder.mkdirs();

		if (!folderCreated) {
			fail("Could not create test folder: " + folder);
		}
	}

	private void createFile(File file) throws IOException {
		if (file.exists()) {
			return;
		}

		boolean fileCreated = file.createNewFile();

		if (!fileCreated) {
			fail("Could not creates test manifest" + file);
		}
	}
}
