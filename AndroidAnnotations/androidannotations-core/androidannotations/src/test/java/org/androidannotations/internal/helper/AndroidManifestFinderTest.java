/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AndroidManifestFinderTest {

	private static final String GRADLE_GEN_FOLDER = "build/generated/source/apt/debug";

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

		Object[] gradleManifestFoundInManifests = { GRADLE_GEN_FOLDER, "build/intermediates/manifests/full/debug", true };
		Object[] gradleManifestFoundInBundles = { GRADLE_GEN_FOLDER, "build/bundles/debug", true };

		Object[] mavenManifestFoundInTarget = { MAVEN_GEN_FOLDER, "target", true };
		Object[] mavenManifestFoundInSrc = { MAVEN_GEN_FOLDER, "src/main", true };
		Object[] mavenManifestFoundInRoot = { MAVEN_GEN_FOLDER, "", true };

		Object[] eclipseManifestFound = { ECLIPSE_GEN_FOLDER, "", true };

		Object[] gradleManifestNotFound = { GRADLE_GEN_FOLDER, "", false };

		Object[] mavenManifestNotFound = { MAVEN_GEN_FOLDER, "something", false };

		Object[] eclipseManifestNotFound = { ECLIPSE_GEN_FOLDER, "something", false };

		Object[] noGeneratedFolderFound = { "", "", false };

		return Arrays.asList(gradleManifestFoundInManifests, gradleManifestFoundInBundles, mavenManifestFoundInTarget, mavenManifestFoundInSrc, mavenManifestFoundInRoot, eclipseManifestFound,
				gradleManifestNotFound, mavenManifestNotFound, eclipseManifestNotFound, noGeneratedFolderFound);
	}

	@Test
	public void testFindManifestInKnownPathsStartingFromGenFolder() throws IOException {
		AndroidManifestFinder finder = new AndroidManifestFinder(null);
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