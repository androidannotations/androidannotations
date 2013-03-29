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

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;

import android.view.View;

@EActivity(R.layout.clickable_widgets)
public class FocusChangeHandledActivity extends EventsHandledAbstractActivity {

	@FocusChange
	public void conventionButton(View evt, boolean hasFocus) {

	}

	@FocusChange
	public void snakeCaseButton() {

	}

	@FocusChange
	public void extendedConventionButton(View evt) {

	}

	@FocusChange(R.id.configurationOverConventionButton)
	public void overridenConventionButton() {

	}

	@FocusChange
	public void buttonWithViewArgument() {

	}

	@FocusChange({ R.id.button1, R.id.button2 })
	public void multipleButtonWithViewArgument(View v, boolean hasFocus) {

	}

}
