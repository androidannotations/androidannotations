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


public class ExtraInstruction extends AbstractInstruction {

	private static final String FORMAT = //
	"" + //
			"        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(\"%s\")) {\n" + //
			"        	try {\n" + //
			"        		%s = extractAndCastExtra_(\"%s\");\n" + //
			"        	} catch (ClassCastException e) {\n" + //
			"        		Log.e(\"%s\", \"Could not cast extra to expected type, the field is left to its default value\", e);\n" + //
			"        	}\n" + //
			"        }\n" + //
			"\n";

	private final String className;

	private final String fieldName;

	private final String key;

	public ExtraInstruction(String className, String fieldName, String key) {
		this.className = className;
		this.fieldName = fieldName;
		this.key = key;
		addImports("android.util.Log");
	}

	@Override
	public String generate() {
		return String.format(FORMAT, key, fieldName, key, className);
	}

}
