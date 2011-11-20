/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.test15;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.util.Log;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Trace;

@EActivity 
public class TracedActivity extends Activity {
	
	public boolean tracedMethodCalled = false;
	public boolean voidTracedMethodCalled = false;
	public boolean voidTracedMethodDebugCalled = false;
	public boolean voidTracedMethodVerboseCalled = false;
	public boolean voidTracedMethodWarnCalled = false;
	public boolean voidTracedMethodErrorCalled = false;
	public boolean voidTracedMethodInfoCalled = false;
	
	@Trace
	Object tracedMethod(List<Map<String, List<Set<Void>>>> param1, Void param2) throws IOException {
		tracedMethodCalled = true;
		return null;
	}

	@Trace
	void voidTracedMethod(List<Map<String, List<Set<Void>>>> param1, Void param2) throws IOException {
		voidTracedMethodCalled = true;
	}

	@Trace(tag="TAGGED", level=Log.DEBUG)
	void voidTracedMethodDebug() {
		voidTracedMethodDebugCalled = true;
	}

	@Trace(level=Log.VERBOSE)
	void voidTracedMethodVerbose() {
		voidTracedMethodVerboseCalled = true;
	}

	@Trace(level=Log.WARN)
	void voidTracedMethodWarn() {
		voidTracedMethodWarnCalled = true;
	}

	@Trace(level=Log.ERROR)
	void voidTracedMethodError() {
		voidTracedMethodErrorCalled = true;
	}

	@Trace(level=Log.INFO)
	void voidTracedMethodInfo() {
		voidTracedMethodInfoCalled = true;
	}

}
