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

public class Robo10ActivityBodyInstruction extends AbstractInstruction {

	private static final String CODE = "" + //
			"    protected ContextScope scope_;\n" + //
			"\n" + //
			"    @Override\n" + //
			"    public Object onRetainNonConfigurationInstance() {\n" + //
			"        return this;\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onRestart() {\n" + //
			"        scope_.enter(this);\n" + //
			"        super.onRestart();\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onStart() {\n" + //
			"        scope_.enter(this);\n" + //
			"        super.onStart();\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onResume() {\n" + //
			"        scope_.enter(this);\n" + //
			"        super.onResume();\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onPause() {\n" + //
			"        super.onPause();\n" + //
			"        scope_.exit(this);\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onNewIntent(Intent intent ) {\n" + //
			"        super.onNewIntent(intent);\n" + //
			"        scope_.enter(this);\n" + //
			"    }\n" + //
			"\n" + //
			"    public Injector getInjector() {\n" + //
			"        return ((InjectorProvider) getApplication()).getInjector();\n" + //
			"    }\n" + //
			"\n";

	public Robo10ActivityBodyInstruction() {
		addImports("roboguice.inject.ContextScope", //
				"android.content.Intent", //
				"com.google.inject.Injector", //
				"roboguice.inject.InjectorProvider");
	}

	@Override
	public String generate() {
		return CODE;
	}

}
