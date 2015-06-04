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

import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EActivity;

import android.widget.CheckBox;
import android.widget.CompoundButton;

@EActivity(R.layout.checkable_widgets)
public class CheckedChangeHandledActivity extends EventsHandledAbstractActivity {

	CompoundButton button;
	boolean checked;

	@CheckedChange
	public void conventionButton(CompoundButton evt, boolean checked) {
		conventionButtonEventHandled = true;
		button = evt;
		this.checked = checked;
	}

	@CheckedChange
	public void checkBox(CheckBox evt, boolean checked) {
		button = evt;
	}

	@CheckedChange
	public void snakeCaseButton(boolean checked, CompoundButton evt) {
		snakeCaseButtonEventHandled = true;
	}

	@CheckedChange
	public void extendedConventionButton(CompoundButton evt) {
		extendedConventionButtonEventHandled = true;
	}

	@CheckedChange(R.id.configurationOverConventionButton)
	public void overridenConventionButton() {
		overridenConventionButtonEventHandled = true;
	}

	@CheckedChange
	public void buttonWithViewArgument(boolean checked) {

	}

	@CheckedChange({ R.id.button1, R.id.button2 })
	public void multipleButtonWithViewArgument(CompoundButton v, boolean checked) {

	}
}
