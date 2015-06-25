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
import org.androidannotations.annotations.LongClick;

import android.view.View;
import android.widget.Button;

@EActivity(R.layout.clickable_widgets)
public class LongClicksHandledActivity extends EventsHandledAbstractActivity {

	@LongClick(R.id.stackOverflowProofButton)
	public void onLongClick(View v) {
		avoidStackOverflowEventHandled = true;
	}

	@LongClick
	public void conventionButton() {
		conventionButtonEventHandled = true;
	}

	@LongClick
	public void buttonWithButtonArgument(Button button) {
		viewArgument = button;
	}

	@LongClick
	public void snakeCaseButton() {
		snakeCaseButtonEventHandled = true;
	}

	@LongClick
	public void extendedConventionButtonLongClicked() {
		extendedConventionButtonEventHandled = true;
	}

	@LongClick(R.id.configurationOverConventionButton)
	public void overridenConventionButton() {
		overridenConventionButtonEventHandled = true;
	}

	public void unboundButton() {
		unboundButtonEventHandled = true;
	}

	@LongClick
	public void buttonWithViewArgument(View viewArgument) {
		this.viewArgument = viewArgument;
	}

	@LongClick({ R.id.button1, R.id.button2 })
	public void multipleButtonWithViewArgument(View viewArgument) {
		this.viewArgument = viewArgument;
		multipleButtonsEventHandled = true;
	}

}
