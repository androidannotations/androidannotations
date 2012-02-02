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
package com.googlecode.androidannotations.test15.eview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.Button;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EView;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.test15.R;

@EView
public class CustomButton extends Button {

	@StringRes(R.string.app_name)
	protected String res;
	
	@AnimationRes(R.anim.fadein)
	protected Animation anim;

	public CustomButton(Context context, int i) {
		super(context);
	}
	
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Trace
	@AfterViews
	protected void afterViews(){
	}
	
	@Background
	protected void someBackgroundTask(){
	}
	
	@UiThread
	protected void someUIThreadTask(){
	}

}
