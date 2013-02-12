/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package org.androidannotations.processing;

import junit.framework.Assert;

import org.junit.Test;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class EBeansHolderTest {

	private EBeansHolder eBeansHolder;

	public EBeansHolderTest() {
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

		checkForFullyQualifiedClassName("java.util.Map<java.lang.String, java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.lang.String[], java.lang.Integer>[]");
	}

	@Test
	public void testRefClass_generics_inner_args() {
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>,java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>[],java.lang.Integer>");
		checkForFullyQualifiedClassName("java.util.Map<java.util.Set<java.lang.String>[],java.lang.Integer>[]");

		checkForFullyQualifiedClassName("java.util.Map < java.util.Set < java.lang.String > , java.lang.Integer >");
	}

}
