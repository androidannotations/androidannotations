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

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import android.view.View;
import android.widget.Button;

@EActivity(R.layout.clickable_widgets)
public class ClicksHandledActivity extends EventsHandledAbstractActivity {

	@Click(R.id.stackOverflowProofButton)
	public void onClick(View v) {
		avoidStackOverflowEventHandled = true;
	}

	@Click(resName = { "libResButton1", "libResButton2" })
	public void libResButton() {
		libResButtonEventHandled = true;
	}

	@Click
	public void conventionButton() {
		conventionButtonEventHandled = true;
	}

	@Click
	public void snakeCaseButton() {
		snakeCaseButtonEventHandled = true;
	}

	@Click
	public void extendedConventionButtonClicked() {
		extendedConventionButtonEventHandled = true;
	}

	@Click(R.id.configurationOverConventionButton)
	public void overridenConventionButton() {
		overridenConventionButtonEventHandled = true;
	}

	public void unboundButton() {
		unboundButtonEventHandled = true;
	}

	@Click
	public void buttonWithViewArgument(View viewArgument) {
		this.viewArgument = viewArgument;
	}

	@Click
	public void buttonWithButtonArgument(Button viewArgument) {
		this.viewArgument = viewArgument;
	}

	@Click({ R.id.button1, R.id.button2 })
	public void multipleButtonWithViewArgument(View viewArgument) {
		this.viewArgument = viewArgument;
		multipleButtonsEventHandled = true;
	}

}
