package com.googlecode.androidannotations.processing;

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
		JClass refClass = eBeansHolder.refClass(fullyQualifiedClassName);
		System.out.println(refClass.fullName());
		Assert.assertEquals(fullyQualifiedClassName, refClass.fullName());
	}

	@Test
	public void testRefClass_generics_one_arg() {
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
	}

	@Test
	public void testRefClass_generics_inner_args() {
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>,java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>[],java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>[],java.lang.Integer>[]");
	}

}
