package org.androidannotations.processing;

import junit.framework.Assert;

import org.junit.Test;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class EBeansHolderTests {

	private EBeansHolder eBeansHolder;

	public EBeansHolderTests() {
		eBeansHolder = new EBeansHolder(new JCodeModel());
	}

	private void checkForFullyQualifiedClassName(String fullyQualifiedClassName) {
		checkForFullyQualifiedClassName(fullyQualifiedClassName, fullyQualifiedClassName);
	}

	private void checkForFullyQualifiedClassName(String fullyQualifiedClassName, String expetedClassName) {
		JClass refClass = eBeansHolder.refClass(fullyQualifiedClassName);
		Assert.assertEquals(expetedClassName, refClass.fullName());
	}

	@Test
	public void testRefClass_primitive() {
		checkForFullyQualifiedClassName("int");
	}

	@Test
	public void testRefClass_generics_one_arg() {
		checkForFullyQualifiedClassName("java.util.List<int>");
		checkForFullyQualifiedClassName("java.util.List<java.lang.String>");
		checkForFullyQualifiedClassName("java.util.List<java.lang.String>[]");
		checkForFullyQualifiedClassName("java.util.List<java.lang.String>[][]");
		checkForFullyQualifiedClassName("java.util.List<java.lang.String[]>");
		checkForFullyQualifiedClassName("java.util.List<java.lang.String[]>[]");
	}

	@Test
	public void testRefClass_generics_multiple_args() {
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String,java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String,java.lang.Integer>[]");
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String,java.lang.Integer>[][]");
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String[],java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String[],java.lang.Integer[]>");
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String[],java.lang.Integer>[]");

		checkForFullyQualifiedClassName("java.util.Map<java.lang.String, java.lang.Integer>", "java.util.Map<java.lang.String,java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String[], java.lang.Integer>[]", "java.util.Map<java.lang.String[],java.lang.Integer>[]");
	}

	@Test
	public void testRefClass_generics_inner_args() {
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>,java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>[],java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>[],java.lang.Integer>[]");

		checkForFullyQualifiedClassName("java.util.Map < java.util.Set < java.lang.String > , java.lang.Integer >", "java.util.Map<java.util.Set<java.lang.String>,java.lang.Integer>");
	}

	@Test
	public void testRefClass_wildcards() {
		checkForFullyQualifiedClassName("java.util.List<?>", "java.util.List<? extends java.lang.Object>");
		checkForFullyQualifiedClassName("java.util.Map<?,?>", "java.util.Map<? extends java.lang.Object,? extends java.lang.Object>");
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String,?>", "java.util.Map<java.lang.String,? extends java.lang.Object>");
	}

	@Test
	public void testRefClass_typevar_simple() {
		checkForFullyQualifiedClassName("java.util.List<T>");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRefClass_typevar_extends() {
		checkForFullyQualifiedClassName("java.util.Set<T extends Object>");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRefClass_typevar_multiple() {
		checkForFullyQualifiedClassName("java.util.Map<T extends java.lang.String, ?>");
	}

}
