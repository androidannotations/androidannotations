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
package org.androidannotations.test.keyevents;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.KeyLongPress;
import org.androidannotations.annotations.KeyMultiple;
import org.androidannotations.annotations.KeyUp;

import android.app.Activity;
import android.view.KeyEvent;

@EActivity
public class KeyEventsActivity extends Activity {

	boolean isEnterDown = false;
	boolean isUKeyUp = false;
	boolean isWMultiple = false;
	boolean isNineMultiple = false;
	int nineMultipleCount;
	boolean isELongPressed = false;
	boolean isELongPressedInAnnotatedClass = false;

	@KeyDown
	void enterPressed() {
		isEnterDown = true;
	}

	@KeyUp
	void u() {
		isUKeyUp = true;
	}

	@KeyMultiple({ KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_W })
	void multiple(int count, KeyEvent keyEvent) {
		switch (keyEvent.getKeyCode()) {
		case KeyEvent.KEYCODE_W:
			isWMultiple = true;
			break;
		case KeyEvent.KEYCODE_9:
			isNineMultiple = true;
			nineMultipleCount = count;
		}
	}

	@KeyLongPress(KeyEvent.KEYCODE_E)
	boolean eLongPress() {
		isELongPressed = true;
		return false;
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_E) {
			isELongPressedInAnnotatedClass = true;
		}
		return super.onKeyLongPress(keyCode, event);
	}
}
