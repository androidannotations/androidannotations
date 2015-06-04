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
package org.androidannotations.test15;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Touch;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

@EActivity(R.layout.clickable_widgets)
public class TouchesHandledActivity extends EventsHandledAbstractActivity {

	@Touch
	public void conventionButton(MotionEvent evt) {
		conventionButtonEventHandled = true;
	}

	@Touch
	public void snakeCaseButton(MotionEvent evt) {
		snakeCaseButtonEventHandled = true;
	}

	@Touch
	public void extendedConventionButtonTouched(MotionEvent evt) {
		extendedConventionButtonEventHandled = true;
	}

	@Touch(R.id.configurationOverConventionButton)
	public void overridenConventionButton(MotionEvent evt) {
		overridenConventionButtonEventHandled = true;
	}

	public void unboundButton(MotionEvent evt) {
		unboundButtonEventHandled = true;
	}

	@Touch
	public void buttonWithViewArgument(MotionEvent evt, View viewArgument) {
		this.viewArgument = viewArgument;
	}

	@Touch
	public void buttonWithButtonArgument(MotionEvent evt, Button button) {
		viewArgument = button;
	}

	@Touch
	public void buttonWithOnlyViewArgument(View viewArgument) {
	}

	@Touch({ R.id.button1, R.id.button2 })
	public void multipleButtonWithViewArgument(MotionEvent evt,
			View viewArgument) {
		this.viewArgument = viewArgument;
		multipleButtonsEventHandled = true;
	}

}
