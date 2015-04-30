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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLog.LogItem;

import android.content.Intent;
import android.net.Uri;

@RunWith(RobolectricTestRunner.class)
public class TracedActivityTest {

	private TracedActivity activity;

	@Before
	public void setUp() {
		activity = Robolectric.buildActivity(TracedActivity_.class).create().start().resume().get();
	}

	@Test
	public void servicesAreInjected() throws IOException {

		assertThat(activity.tracedMethodCalled).isFalse();
		activity.tracedMethod(null, null);
		assertThat(activity.tracedMethodCalled).isTrue();

		assertThat(activity.voidTracedMethodCalled).isFalse();
		activity.voidTracedMethod(null, null);
		assertThat(activity.voidTracedMethodCalled).isTrue();

		assertThat(activity.voidTracedMethodDebugCalled).isFalse();
		activity.voidTracedMethodDebug();
		assertThat(activity.voidTracedMethodDebugCalled).isTrue();

		assertThat(activity.voidTracedMethodErrorCalled).isFalse();
		activity.voidTracedMethodError();
		assertThat(activity.voidTracedMethodErrorCalled).isTrue();

		assertThat(activity.voidTracedMethodInfoCalled).isFalse();
		activity.voidTracedMethodInfo();
		assertThat(activity.voidTracedMethodInfoCalled).isTrue();

		assertThat(activity.voidTracedMethodVerboseCalled).isFalse();
		activity.voidTracedMethodVerbose();
		assertThat(activity.voidTracedMethodVerboseCalled).isTrue();

		assertThat(activity.voidTracedMethodWarnCalled).isFalse();
		activity.voidTracedMethodWarn();
		assertThat(activity.voidTracedMethodWarnCalled).isTrue();

		assertThat(activity.overloadedMethodInt).isFalse();
		activity.overloadedMethod(0);
		assertThat(activity.overloadedMethodInt).isTrue();

		assertThat(activity.overloadedMethodIntFLoat).isFalse();
		activity.overloadedMethod(0, 0f);
		assertThat(activity.overloadedMethodIntFLoat).isTrue();
	}

	@Test
	public void noReturnNoParam() {
		activity.noReturnNoParam();

		assertTrue(logContains("Entering [void noReturnNoParam()]"));
		assertTrue(logContains("Exiting [void noReturnNoParam()], duration in ms: "));
	}

	@Test
	public void noReturnStringParam() {
		activity.noReturnStringParam("test");

		assertTrue(logContains("Entering [void noReturnStringParam(param = test)]"));
		assertTrue(logContains("Exiting [void noReturnStringParam(String)], duration in ms: "));
	}

	@Test
	public void noReturnIntArrayParam() {
		activity.noReturnIntArrayParam(new int[] { 1, 2, 3 });

		assertTrue(logContains("Entering [void noReturnIntArrayParam(param = [1, 2, 3])]"));
		assertTrue(logContains("Exiting [void noReturnIntArrayParam(int[])], duration in ms: "));
	}

	@Test
	public void noReturnStringAndIntArrayParam() {
		activity.noReturnStringAndIntArrayParam("test", new int[] { 1, 2, 3 });

		assertTrue(logContains("Entering [void noReturnStringAndIntArrayParam(param1 = test, param2 = [1, 2, 3])]"));
		assertTrue(logContains("Exiting [void noReturnStringAndIntArrayParam(String, int[])], duration in ms: "));
	}

	@Test
	public void noReturnIntentParam() {
		activity.noReturnIntentParam(new Intent("TEST", Uri.parse("http://www.androidannotations.org")));

		assertTrue(logContains("Entering [void noReturnIntentParam(param = Intent{action=TEST, extras=Bundle[{}], data=http://www.androidannotations.org})]"));
		assertTrue(logContains("Exiting [void noReturnIntentParam(Intent)], duration in ms: "));
	}

	@Test
	public void booleanReturnNoParam() {
		activity.booleanReturnNoParam();

		assertTrue(logContains("Entering [boolean booleanReturnNoParam()]"));
		assertTrue(logContains("Exiting [boolean booleanReturnNoParam() returning: true], duration in ms: "));
	}

	@Test
	public void stringReturnStringParam() {
		activity.stringReturnStringParam("test");

		assertTrue(logContains("Entering [java.lang.String stringReturnStringParam(param = test)]"));
		assertTrue(logContains("Exiting [java.lang.String stringReturnStringParam(String) returning: test], duration in ms: "));
	}

	public void intArrayReturnIntArrayParam() {
		activity.intArrayReturnIntArrayParam(new int[] { 1, 2, 3 });

		assertTrue(logContains("Entering [int[] intArrayReturnIntArrayParam(param = [1, 2, 3])]"));
		assertTrue(logContains("Exiting [int[] intArrayReturnIntArrayParam(int[]) returning: [1, 2, 3]], duration in ms: "));
	}

	public void intentReturnIntentParam() {
		activity.intentReturnIntentParam(new Intent("TEST", Uri.parse("http://www.androidannotations.org")));

		assertTrue(logContains("Entering [java.lang.String intentReturnIntentParam(param = Intent{action=TEST, extras=Bundle[{}], data=http://www.androidannotations.org})]"));
		assertTrue(logContains("Exiting [java.lang.String intentReturnIntentParam(Intent) returning: test], duration in ms: "));
	}

	/**
	 * Check if a message has been logged
	 *
	 * @param msg
	 * @return {@code true} if a log entry starting with {@code msg} exists
	 */
	private static boolean logContains(String msg) {
		List<LogItem> logs = ShadowLog.getLogs();
		boolean found = false;
		for (LogItem logItem : logs) {
			if (logItem.msg.startsWith(msg)) {
				found = true;
			}
		}
		return found;
	}
}
