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
import java.util.List;

public class MetaActivity {

	private static final String CLASS_SUFFIX = "_";

	private final String packageName;

	private final String superClassName;

	private final String layoutQualifiedName;
	
	private final List<Instruction> onCreateInstructions = new ArrayList<Instruction>();
	
	private final List<Instruction> memberInstructions = new ArrayList<Instruction>();

	public MetaActivity(String packageName, String superClassName, String layoutQualifiedName) {
		this.packageName = packageName;
		this.superClassName = superClassName;
		this.layoutQualifiedName = layoutQualifiedName;
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

}
