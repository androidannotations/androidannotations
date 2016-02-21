/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.test.keyevents;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.view.KeyEvent;

@RunWith(RobolectricTestRunner.class)
public class KeyEventsActivityTest {

	private KeyEventsActivity activity;

	@Before
	public void setUp() {
		activity = Robolectric.buildActivity(KeyEventsActivity_.class).create().get();
	}

	@Test
	public void subclassTakesPrecedenceInKeyEventHandling() {
		KeyEvent keyEvent = mock(KeyEvent.class);
		int keyCode = KeyEvent.KEYCODE_E;

		activity.onKeyLongPress(keyCode, keyEvent);

		assertThat(activity.isELongPressed).isTrue();
		assertThat(activity.isELongPressedInAnnotatedClass).isFalse();
	}

	@Test
	public void keyDownKeyCodeNameFromMethod() {
		KeyEvent keyEvent = mock(KeyEvent.class);
		int keyCode = KeyEvent.KEYCODE_ENTER;

		activity.onKeyDown(keyCode, keyEvent);

		assertThat(activity.isEnterDown).isTrue();
	}

	@Test
	public void keyUpKeyCodeNameFromMethod() {
		KeyEvent keyEvent = mock(KeyEvent.class);
		int keyCode = KeyEvent.KEYCODE_U;

		activity.onKeyUp(keyCode, keyEvent);

		assertThat(activity.isUKeyUp).isTrue();
	}

	@Test
	public void multipleArguments() {
		KeyEvent keyEvent = mock(KeyEvent.class);
		int keyCode = KeyEvent.KEYCODE_W;
		int count = 1;

		when(keyEvent.getKeyCode()).thenReturn(keyCode);

		activity.onKeyMultiple(keyCode, count, keyEvent);

		assertThat(activity.isWMultiple).isTrue();
		assertThat(activity.isNineMultiple).isFalse();
	}

	@Test
	public void keyMultipleWithCount() {
		KeyEvent keyEvent = mock(KeyEvent.class);
		int keyCode = KeyEvent.KEYCODE_9;
		int count = 9;

		when(keyEvent.getKeyCode()).thenReturn(keyCode);

		activity.onKeyMultiple(keyCode, count, keyEvent);

		assertThat(activity.isNineMultiple).isTrue();
		assertThat(activity.nineMultipleCount).isEqualTo(count);
	}

	@Test
	public void goodMethodReturnIfKeyLongPress() {
		KeyEvent keyEvent = mock(KeyEvent.class);
		int keyCode = KeyEvent.KEYCODE_E;

		boolean eKeyLongPressReturn = activity.onKeyLongPress(keyCode, keyEvent);

		assertThat(activity.isELongPressed).isTrue();
		assertThat(eKeyLongPressReturn).isFalse();
	}
}
