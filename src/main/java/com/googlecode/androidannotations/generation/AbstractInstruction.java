/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.generation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.androidannotations.model.Instruction;

/**
 * An abstract class to help implementing the {@link Instruction} interface.
 * Currently only adds support for imports, with the
 * {@link #addImports(String...)} method.
 * 
 */
public abstract class AbstractInstruction implements Instruction {

	private final Set<String> instructionImports = new HashSet<String>();
	
	private final Set<String> instructionStaticImports = new HashSet<String>();

	protected void addImports(String... imports) {
		instructionImports.addAll(Arrays.asList(imports));
	}
	
	protected void addStaticImports(String... imports) {
		instructionStaticImports.addAll(Arrays.asList(imports));
	}

	@Override
	public final Set<String> getImports() {
		return instructionImports;
	}
	
	@Override
	public final Set<String> getStaticImports() {
		return instructionStaticImports;
	}
}
