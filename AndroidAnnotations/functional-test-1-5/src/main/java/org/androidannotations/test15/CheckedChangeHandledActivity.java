/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
import org.androidannotations.annotations.FocusChange;

import android.view.View;
import android.widget.CompoundButton;

@EActivity(R.layout.clickable_widgets)
public class CheckedChangeHandledActivity extends EventsHandledAbstractActivity {

	@CheckedChange
	public void conventionButton(CompoundButton evt, boolean hasFocus) {

	}

	@CheckedChange
	public void snakeCaseButton() {

	}

	@CheckedChange
	public void extendedConventionButton(CompoundButton evt) {

	}

	@CheckedChange(R.id.configurationOverConventionButton)
	public void overridenConventionButton() {

	}

	@CheckedChange
	public void buttonWithViewArgument() {

	}

	@CheckedChange({ R.id.button1, R.id.button2 })
	public void multipleButtonWithViewArgument(CompoundButton v, boolean hasFocus) {

	}

}
