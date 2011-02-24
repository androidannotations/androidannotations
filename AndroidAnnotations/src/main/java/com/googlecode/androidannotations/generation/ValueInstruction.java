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


public class ValueInstruction extends AbstractInstruction {

	private static final String VALUE_FORMAT = "        %s = getResources().%s(%s);\n";

	private final String fieldName;
	
	private final String methodName;

	private final String qualifiedId;

	public ValueInstruction(String fieldName, String methodName, String qualifiedId) {
		this.fieldName = fieldName;
		this.qualifiedId = qualifiedId;
		this.methodName = methodName;
	}

	@Override
	public String generate() {
		return String.format(VALUE_FORMAT, fieldName, methodName, qualifiedId);
	}

}
