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

import com.googlecode.androidannotations.model.Instruction;

public class UiThreadInstruction implements Instruction {

	private static final String FORMAT = //
	"" + //
			"        @Override\n" + //
			"        protected void %s() {\n" + //
			"		 	runOnUiThread(new Runnable() {\n" + //
			"		      public void run() {\n" + //
			"               %s.super.%s();\n" + //
			"		      }\n" + //
			"           });\n" + //
			"		 }\n" + //
			"\n";

	private final String methodName;

	private final String className;


	public UiThreadInstruction(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}

	@Override
	public String generate() {
		return String.format(FORMAT, methodName, className, methodName);
	}

}
