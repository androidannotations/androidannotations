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
package org.androidannotations.test15.eview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.AnimationRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.test15.R;

@EView
public class CustomButton extends Button {

	@StringRes(R.string.app_name)
	String res;

	@AnimationRes(R.anim.fadein)
	Animation anim;

	public boolean afterViewsCalled = false;

	public int constructorParameter;

	public CustomButton(Context context) {
		super(context);
	}

	public CustomButton(Context context, int constructorParameter) {
		super(context);
		this.constructorParameter = constructorParameter;
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Trace
	@AfterViews
	void afterViews() {
		afterViewsCalled = true;
	}

	@Background
	void someBackgroundTask() {
	}

	@UiThread
	void someUIThreadTask() {
	}

}
