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
package org.androidannotations.keyevents;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.KeyMultiple;

import android.app.Activity;
import android.view.KeyEvent;

@EActivity
public class ActivityWithKeyEvents extends Activity {

	@KeyDown
	void a() {
	}

	@KeyDown
	void onB() {
	}

	@KeyDown
	void cPressed() {
	}

	@KeyDown
	void onDPressed() {
	}

	@KeyDown(KeyEvent.KEYCODE_E)
	void e() {
	}

	@KeyDown
	void f(KeyEvent keyEvent) {
	}

	@KeyDown(KeyEvent.KEYCODE_G)
	void g(KeyEvent keyEvent) {
	}

	@KeyDown({ KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_1 })
	void digits1(KeyEvent keyEvent) {
	}

	@KeyDown({ KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3 })
	void digits2() {
	}

	@KeyDown
	boolean h() {
		return true;
	}

	@KeyDown
	boolean i(KeyEvent keyEvent) {
		return true;
	}

	@KeyDown(KeyEvent.KEYCODE_J)
	boolean j() {
		return true;
	}

	@KeyDown({ KeyEvent.KEYCODE_K, KeyEvent.KEYCODE_L })
	boolean kl() {
		return true;
	}

	@KeyMultiple
	void m() {
	}

	@KeyMultiple
	void n(int count) {
	}

	@KeyMultiple
	void o(KeyEvent keyEvent) {
	}

	@KeyMultiple
	void p(int count, KeyEvent keyEvent) {
	}

	@KeyMultiple
	void q(KeyEvent keyEvent, int count) {
	}

	@KeyMultiple
	boolean r() {
		return true;
	}

	@KeyMultiple
	boolean s(KeyEvent event, int count) {
		return true;
	}

	@KeyMultiple
	boolean t(int count, KeyEvent event) {
		return true;
	}

	@KeyMultiple(KeyEvent.KEYCODE_U)
	boolean u(int count) {
		return true;
	}

	@KeyMultiple({ KeyEvent.KEYCODE_V, KeyEvent.KEYCODE_W })
	boolean vw(KeyEvent event) {
		return true;
	}

}
