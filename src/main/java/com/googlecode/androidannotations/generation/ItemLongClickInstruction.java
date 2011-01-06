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

/**
 * @author Benjamin Fellous
 */
public class ItemLongClickInstruction implements Instruction {

	private static final String FORMAT = //
	"" + //
			"        ((android.widget.AdapterView<?>) findViewById(%s)).setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {\n" + //
			"			@Override\n" + //
			"			public boolean onItemLongClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {\n" + //
			"				return %s(parent, view, position, id);\n" + //
			"			}\n" + //
			"		});\n" + //
			"\n";

	private final String methodName;

	private final String clickQualifiedId;

	public ItemLongClickInstruction(String methodName, String clickQualifiedId) {
		this.methodName = methodName;
		this.clickQualifiedId = clickQualifiedId;
	}

	@Override
	public String generate() {
		return String.format(FORMAT, clickQualifiedId, methodName);
	}

}
