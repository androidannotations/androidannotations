/**
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
package org.androidannotations.databinding;

import java.io.File;
import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataBindingTest extends AAProcessorTestHelper {

	// CHECKSTYLE:OFF
	private static final String[] DATA_BINDING_EXPRESSIONS = new String[] { "        ViewGroup contentView = internalFindViewById(android.R.id.content);",
			"        viewDataBinding_ = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_main, contentView, false);",
			"        setContentView(viewDataBinding_.getRoot(), viewDataBinding_.getRoot().getLayoutParams());", };

	private static final String[] INJECT_DATA_BINDING_FIELD_EXPRESSIONS = new String[] { "        this.bindingField = ((org.androidannotations.databinding.ActivityBinding) viewDataBinding_);", };

	private static final String[] INJECT_DATA_BINDING_METHOD_EXPRESSIONS = new String[] { "            binding = ((org.androidannotations.databinding.ActivityBinding) viewDataBinding_);",
			"            bindingMethod(binding);", };

	private static final String[] INJECT_DATA_BINDING_PARAM_EXPRESSIONS = new String[] { "            bindingParam = ((org.androidannotations.databinding.ActivityBinding) viewDataBinding_);",
			"            injectBinding(bindingParam);", };

	private static final String[] DATA_BINDING_EXPRESSIONS_FRAGMENT = new String[] {
			"            viewDataBinding_ = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.activity_main, container, false);",
			"            contentView_ = viewDataBinding_.getRoot();", };

	private static final String[] DATA_BINDING_EXPRESSIONS_VIEWGROUP = new String[] {
			"            viewDataBinding_ = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_main, this, true);", };
	// CHECKSTYLE:ON

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
		addManifestProcessorParameter(DataBindingTest.class);
	}

	@After
	public void tearDown() {
		ensureOutputDirectoryIsEmpty();
	}

	@Test
	public void activityWithoutDataBindingAnnotationDoesNotCompile() throws IOException {
		CompileResult result = compileFiles(ActivityWithoutDataBoundAnnotation.class, getDataBindingUtilClass());

		assertCompilationErrorCount(1, result);
		assertCompilationErrorOn(ActivityWithoutDataBoundAnnotation.class, "@BindingObject", result);
	}

	@Test
	public void activityWithoutDataBindingOnClasspathDoesNotCompile() throws IOException {
		CompileResult result = compileFiles(ActivityWithDataBoundAnnotation.class);
		assertCompilationErrorCount(1, result);
		assertCompilationErrorOn(ActivityWithDataBoundAnnotation.class, "@DataBound", result);
	}

	@Test
	public void activityWithWrongAfterDataBindingParametersDoesNotCompile() throws IOException {
		CompileResult result = compileFiles(ActivityWithWrongDataBindingMethodParameters.class, getDataBindingUtilClass());
		assertCompilationErrorCount(5, result);
		assertCompilationErrorOn(ActivityWithWrongDataBindingMethodParameters.class, "@BindingObject", result);
	}

	@Test
	public void activityWithDataBoundAnnotationCompiles() {
		CompileResult result = compileFiles(ActivityWithDataBoundAnnotation.class, getDataBindingUtilClass());
		assertCompilationSuccessful(result);

		File generatedFile = toGeneratedFile(ActivityWithDataBoundAnnotation.class);

		assertGeneratedClassContains(generatedFile, DATA_BINDING_EXPRESSIONS);
		assertGeneratedClassContains(generatedFile, INJECT_DATA_BINDING_FIELD_EXPRESSIONS);
		assertGeneratedClassContains(generatedFile, INJECT_DATA_BINDING_METHOD_EXPRESSIONS);
		assertGeneratedClassContains(generatedFile, INJECT_DATA_BINDING_PARAM_EXPRESSIONS);
	}

	@Test
	public void fragmentWithDataBoundAnnotationCompiles() {
		CompileResult result = compileFiles(FragmentWithDataBoundAnnotation.class, getDataBindingUtilClass());
		assertCompilationSuccessful(result);

		File generatedFile = toGeneratedFile(FragmentWithDataBoundAnnotation.class);

		assertGeneratedClassContains(generatedFile, DATA_BINDING_EXPRESSIONS_FRAGMENT);
	}

	@Test
	public void viewGroupWithDataBoundAnnotationCompiles() {
		CompileResult result = compileFiles(ViewGroupWithDataBoundAnnotation.class, getDataBindingUtilClass());
		assertCompilationSuccessful(result);

		File generatedFile = toGeneratedFile(ViewGroupWithDataBoundAnnotation.class);

		assertGeneratedClassContains(generatedFile, DATA_BINDING_EXPRESSIONS_VIEWGROUP);
	}

	@Test
	public void beanWithDataBoundAnnotationDoesNotCompile() throws IOException {
		CompileResult result = compileFiles(BeanWithDataBoundAnnotation.class, getDataBindingUtilClass());

		assertCompilationErrorCount(1, result);
		assertCompilationErrorOn(BeanWithDataBoundAnnotation.class, "@DataBound", result);
	}

	private String getDataBindingUtilClass() {
		return toPath(DataBindingTest.class, "DataBindingUtil.java");
	}

}
