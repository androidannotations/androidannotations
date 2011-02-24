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
package com.googlecode.androidannotations.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetaActivity {

	public static final String CLASS_SUFFIX = "_";
	
	private static Set<String> getImports(List<Instruction> instructions) {
		Set<String> imports = new HashSet<String>();
		for(Instruction instruction : instructions) {
			imports.addAll(instruction.getImports());
		}
		return imports;
	}
	
	private static Set<String> getStaticImports(List<Instruction> instructions) {
		Set<String> imports = new HashSet<String>();
		for(Instruction instruction : instructions) {
			imports.addAll(instruction.getStaticImports());
		}
		return imports;
	}

	private final String packageName;

	private final String superClassName;

	private final String layoutQualifiedName;
	
	private final List<Instruction> onCreateInstructions = new ArrayList<Instruction>();
	
	private final List<Instruction> beforeCreateInstructions = new ArrayList<Instruction>();
	
	private final List<Instruction> memberInstructions = new ArrayList<Instruction>();
	
	private final List<String> implementedInterfaces = new ArrayList<String>();

	public MetaActivity(String packageName, String superClassName, String layoutQualifiedName) {
		this.packageName = packageName;
		this.superClassName = superClassName;
		this.layoutQualifiedName = layoutQualifiedName;
	}
	
	public Set<String> getImports() {
		Set<String> imports = new HashSet<String>();
		imports.addAll(getImports(onCreateInstructions));
		imports.addAll(getImports(beforeCreateInstructions));
		imports.addAll(getImports(memberInstructions));
		return imports;
	}
	
	public Set<String> getStaticImports() {
		Set<String> imports = new HashSet<String>();
		imports.addAll(getStaticImports(onCreateInstructions));
		imports.addAll(getStaticImports(beforeCreateInstructions));
		imports.addAll(getStaticImports(memberInstructions));
		return imports;
	}

	public String getClassQualifiedName() {
		return packageName + "." + getClassSimpleName();
	}

	public String getClassSimpleName() {
		return superClassName + CLASS_SUFFIX;
	}

	public List<Instruction> getOnCreateInstructions() {
		return onCreateInstructions;
	}
	
	public List<Instruction> getBeforeCreateInstructions() {
		return beforeCreateInstructions;
	}
	
	public List<Instruction> getMemberInstructions() {
		return memberInstructions;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public String getLayoutQualifiedName() {
		return layoutQualifiedName;
	}
	

	public List<String> getImplementedInterfaces() {
		return implementedInterfaces;
	}

}
