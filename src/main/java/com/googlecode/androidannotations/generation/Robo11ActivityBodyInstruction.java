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

import java.util.List;

public class Robo11ActivityBodyInstruction extends AbstractInstruction {

	private static final String LISTENER = "    @java.lang.SuppressWarnings(\"unused\") @com.google.inject.Inject private %s listener%d_;\n";

	private static final String CODE = "" + //
			"    private ContextScope scope_;\n" + //
			"    private EventManager eventManager_;\n" + //
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
			"        eventManager_.fire(new OnRestartEvent());\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onStart() {\n" + //
			"        scope_.enter(this);\n" + //
			"        super.onStart();\n" + //
			"        eventManager_.fire(new OnStartEvent());\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onResume() {\n" + //
			"        scope_.enter(this);\n" + //
			"        super.onResume();\n" + //
			"        eventManager_.fire(new OnResumeEvent());\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onPause() {\n" + //
			"        super.onPause();\n" + //
			"        eventManager_.fire(new OnPauseEvent());\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onNewIntent(Intent intent) {\n" + //
			"        super.onNewIntent(intent);\n" + //
			"        scope_.enter(this);\n" + //
			"        eventManager_.fire(new OnNewIntentEvent());\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onStop() {\n" + //
			"        eventManager_.fire(new OnStopEvent());\n" + //
			"        super.onStop();\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onDestroy() {\n" + //
			"        eventManager_.fire(new OnDestroyEvent());\n" + //
			"        eventManager_.clear(this);\n" + //
			"        scope_.exit(this);\n" + //
			"        super.onDestroy();\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    public void onConfigurationChanged(Configuration newConfig) {\n" + //
			"        super.onConfigurationChanged(newConfig);\n" + //
			"        eventManager_.fire(new OnConfigurationChangedEvent(newConfig));\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    public void onContentChanged() {\n" + //
			"        super.onContentChanged();\n" + //
			"        eventManager_.fire(new OnContentChangedEvent());\n" + //
			"    }\n" + //
			"\n" + //
			"    @Override\n" + //
			"    protected void onActivityResult(int requestCode, int resultCode, Intent data) {\n" + //
			"        super.onActivityResult(requestCode, resultCode, data);\n" + //
			"        eventManager_.fire(new OnActivityResultEvent(requestCode, resultCode, data));\n" + //
			"    }\n" + //
			"\n" + //
			"    public Injector getInjector() {\n" + //
			"        return ((InjectorProvider) getApplication()).getInjector();\n" + //
			"    }\n" + //
			"\n";

	private final List<String> listenerClasses;

	public Robo11ActivityBodyInstruction(List<String> listenerClasses) {
		this.listenerClasses = listenerClasses;
		addImports("roboguice.inject.ContextScope", //
				"android.content.Intent", //
				"com.google.inject.Injector", //
				"roboguice.inject.InjectorProvider", //
				"roboguice.event.EventManager", //
				"roboguice.activity.event.OnRestartEvent", //
				"roboguice.activity.event.OnStartEvent", //
				"roboguice.activity.event.OnResumeEvent", //
				"roboguice.activity.event.OnPauseEvent", //
				"roboguice.activity.event.OnNewIntentEvent", //
				"roboguice.activity.event.OnStopEvent", //
				"roboguice.activity.event.OnDestroyEvent", //
				"android.content.res.Configuration", //
				"roboguice.activity.event.OnConfigurationChangedEvent", //
				"roboguice.activity.event.OnContentChangedEvent", //
				"roboguice.activity.event.OnActivityResultEvent" //
				);
	}

	@Override
	public String generate() {

		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (String listenerClass : listenerClasses) {
			sb.append(String.format(LISTENER, listenerClass, i));
			i++;
		}
		sb.append(CODE);

		return sb.toString();
	}

}
