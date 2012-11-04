package com.googlecode.androidannotations.rest;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.androidannotations.AndroidAnnotationProcessor;
import com.googlecode.androidannotations.utils.AAProcessorTestHelper;

public class RestConverterTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addManifestProcessorParameter(RestConverterTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void client_with_no_converters_compiles() {
		CompileResult result = compileFiles(ClientWithNoConverters.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void client_with_valid_converter_compiles() {
		CompileResult result = compileFiles(ClientWithValidConverter.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void client_with_abstract_converter_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ClientWithAbstractConverter.class);
		assertCompilationErrorOn(ClientWithAbstractConverter.class, "@Rest", result);
	}

	@Test
	public void client_with_non_converter_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ClientWithNonConverter.class);
		assertCompilationErrorOn(ClientWithNonConverter.class, "@Rest", result);
	}

	@Test
	public void client_with_wrong_constructor_converter_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ClientWithWrongConstructorConverter.class);
		assertCompilationErrorOn(ClientWithWrongConstructorConverter.class, "@Rest", result);
	}

}
