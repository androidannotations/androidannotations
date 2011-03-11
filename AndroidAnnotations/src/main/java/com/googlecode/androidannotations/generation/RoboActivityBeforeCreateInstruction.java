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


public class RoboActivityBeforeCreateInstruction extends AbstractInstruction {

	private static final String CODE = "" + //
			"        Injector injector_ = getInjector();\n" + //
			"        scope_ = injector_.getInstance(ContextScope.class);\n" + //
			"        scope_.enter(this);\n" + //
			"        injector_.injectMembers(this);\n" + //
			"        eventManager_ = injector_.getInstance(EventManager.class);\n" + //
			"        eventManager_.fire(new OnCreateEvent(savedInstanceState));\n" + //
			"\n";
	
	public RoboActivityBeforeCreateInstruction() {
		addImports("com.google.inject.Injector", //
				"roboguice.inject.ContextScope", //
				"roboguice.event.EventManager", //
				"roboguice.activity.event.OnCreateEvent");
	}

	@Override
	public String generate() {
		return CODE;
	}

}
