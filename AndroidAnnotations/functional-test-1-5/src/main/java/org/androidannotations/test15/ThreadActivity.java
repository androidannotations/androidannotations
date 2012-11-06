/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test15;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.test15.instancestate.MySerializableBean;

@EActivity
public class ThreadActivity extends Activity {

	@UiThread
	void emptyUiMethod() {

	}

	@Background
	void emptyBackgroundMethod() {

	}

	@UiThread
	void objectUiMethod(Object param) {

	}

	@Background
	void objectBackgroundMethod(Object param) {

	}

	@UiThread
	void genericUiMethod(List<Map<String, List<Set<Void>>>> param) {

	}

	@Background
	void genericBackgroundMethod(List<Map<String, List<Set<MySerializableBean[]>>>> param) {

	}

	@UiThread(delay = 1000)
	void emptyUiDelayedMethod() {

	}
	
	@UiThread
	void uiThreadedUsingArrayParamtersMethod(MySerializableBean [] array) {}

	@UiThread
	void uiThreadedUsingArrayParamtersMethod(MySerializableBean [][] array) {}

	@Background
	void backgrounddUsingArrayParamtersMethod(MySerializableBean [] array) {}

	@Background
	void backgroundUsingArrayParamtersMethod(MySerializableBean [][] array) {}
}
