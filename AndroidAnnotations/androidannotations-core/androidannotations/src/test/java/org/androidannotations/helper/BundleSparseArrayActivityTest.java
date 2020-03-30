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
package org.androidannotations.helper;

import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class BundleSparseArrayActivityTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(BundleSparseArrayActivityTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
		addManifestProcessorParameter(BundleSparseArrayActivityTest.class, "AndroidManifest.xml");
	}

	@Test
	public void compileSuccessForBundleSparseArray() {
		// CHECKSTYLE:OFF
		String[] methodSignature = { //
				"    @Override", //
				"    public void onSaveInstanceState(Bundle bundle_) {", //
				"        super.onSaveInstanceState(bundle_);", //
				"        bundle_.putSparseParcelableArray(\"sparseArrayWithParcelable\", sparseArrayWithParcelable);", //
				"        bundle_.putSparseParcelableArray(\"sparseArrayWithExtendParcelable\", sparseArrayWithExtendParcelable);", //
				"    }", //
				"", //
				"    private void restoreSavedInstanceState_(Bundle savedInstanceState) {", //
				"        if (savedInstanceState == null) {", //
				"            return;", //
				"        }", //
				"        sparseArrayWithParcelable = savedInstanceState.getSparseParcelableArray(\"sparseArrayWithParcelable\");", //
				"        sparseArrayWithExtendParcelable = savedInstanceState.getSparseParcelableArray(\"sparseArrayWithExtendParcelable\");", //
				"    }" };
		// CHECKSTYLE:ON

		CompileResult result = compileFiles(BundleSparseArrayCompileSuccessActivity.class);
		assertCompilationSuccessful(result);
		assertGeneratedClassContains(toGeneratedFile(BundleSparseArrayCompileSuccessActivity.class), methodSignature);
	}

	@Test
	public void compileFailWithNoGenericsType() throws IOException {
		CompileResult result = compileFiles(BundleSparseArrayNoGenericsTypeActivity.class);
		assertCompilationErrorOn(BundleSparseArrayNoGenericsTypeActivity.class, "@InstanceState", result);
	}

	@Test
	public void compileFailWithNonParcelableType() throws IOException {
		CompileResult result = compileFiles(BundleSparseArrayNoParcelableTypeActivity.class);
		assertCompilationErrorOn(BundleSparseArrayNoParcelableTypeActivity.class, "@InstanceState", result);
	}

}
