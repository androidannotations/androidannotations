/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.trace;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.test15.instancestate.MySerializableBean;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

@EActivity
public class TracedActivity extends Activity {

	public boolean tracedMethodCalled = false;
	public boolean voidTracedMethodCalled = false;
	public boolean voidTracedMethodDebugCalled = false;
	public boolean voidTracedMethodVerboseCalled = false;
	public boolean voidTracedMethodWarnCalled = false;
	public boolean voidTracedMethodErrorCalled = false;
	public boolean voidTracedMethodInfoCalled = false;
	public boolean overloadedMethodInt = false;
	public boolean overloadedMethodIntFLoat = false;

	@Trace
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Trace
	Object tracedMethod(List<Map<String, List<Set<Void>>>> param1, Void param2) throws IOException {
		tracedMethodCalled = true;
		return null;
	}

	@Trace
	void voidTracedMethod(List<Map<String, List<Set<Void>>>> param1, Void param2) throws IOException {
		voidTracedMethodCalled = true;
	}

	@Trace(tag = "TAGGED", level = Log.DEBUG)
	void voidTracedMethodDebug() {
		voidTracedMethodDebugCalled = true;
	}

	@Trace(level = Log.VERBOSE)
	void voidTracedMethodVerbose() {
		voidTracedMethodVerboseCalled = true;
	}

	@Trace(level = Log.WARN)
	void voidTracedMethodWarn() {
		voidTracedMethodWarnCalled = true;
	}

	@Trace(level = Log.ERROR)
	void voidTracedMethodError() {
		voidTracedMethodErrorCalled = true;
	}

	@Trace(level = Log.INFO)
	void voidTracedMethodInfo() {
		voidTracedMethodInfoCalled = true;
	}

	@Trace
	void overloadedMethod(int x) {
		overloadedMethodInt = true;
	}

	@Trace
	void overloadedMethod(int x, float f) {
		overloadedMethodIntFLoat = true;
	}

	@Trace
	@UiThread
	void mixedUiThreadMethod() {

	}

	@Trace
	@UiThread(delay = 1000)
	void mixedUiThreadDelayedMethod() {

	}

	@Trace
	@Background
	void mixedBackgroundMethod() {

	}

	@Trace
	@Transactional
	void mixedTransactionalMethod(SQLiteDatabase db) {

	}

	@Trace
	void tracedUsingArrayParameters(MySerializableBean[] array, MySerializableBean[][] multiDimArray) {
	}

	@Trace
	public void noReturnNoParam() {
	}

	@Trace
	public void noReturnStringParam(String param) {
	}

	@Trace
	public void noReturnIntArrayParam(int[] param) {
	}

	@Trace
	public void noReturnStringAndIntArrayParam(String param1, int[] param2) {
	}

	@Trace
	public void noReturnIntentParam(Intent param) {
	}

	@Trace
	public boolean booleanReturnNoParam() {
		return true;
	}

	@Trace
	public String stringReturnStringParam(String param) {
		return param;
	}

	@Trace
	public int[] intArrayReturnIntArrayParam(int[] param) {
		return param;
	}

	@Trace
	public Intent intentReturnIntentParam(Intent param) {
		return param;
	}

}
