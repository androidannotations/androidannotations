/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.test15;

import android.app.Activity;
import android.view.View;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;

@EActivity(R.layout.clicks_handled)
public class ClicksHandledActivity extends Activity{
	
	View viewArgument;
	
	boolean conventionButtonClicked;
	boolean snakeCaseButtonClicked;
	boolean extendedConventionButtonClicked;
	boolean overridenConventionButtonClicked;
	boolean unboundButtonClicked;
	boolean multipleButtonsClicked;

	@Click
	public void conventionButton() {
		conventionButtonClicked = true;
	}
	
	@Click
	public void snakeCaseButton() {
		snakeCaseButtonClicked = true;
	}	

	@Click
	public void extendedConventionButtonClicked() {
		extendedConventionButtonClicked = true;
	}
	
	@Click(R.id.configurationOverConventionButton)
	public void overridenConventionButton() {
		overridenConventionButtonClicked = true;
	}
	
	public void unboundButton() {
		unboundButtonClicked = true;
	}
	
	@Click
	public void buttonWithViewArgument(View viewArgument) {
		this.viewArgument = viewArgument;
	}

	@Click({R.id.button1, R.id.button2})
	public void multpleButtonWithViewArgument(View viewArgument) {
		this.viewArgument = viewArgument;
		multipleButtonsClicked = true;
	}
	
}
