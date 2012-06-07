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
package com.googlecode.androidannotations.test15.eviewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.test15.R;

@EViewGroup(R.layout.component)
public class CustomFrameLayout extends FrameLayout {

	@ViewById(R.id.title)
	protected TextView tv;

	@ViewById
	protected TextView subtitle;
	
	@StringRes(R.string.app_name)
	protected String res;
	
	@AnimationRes(R.anim.fadein)
	protected Animation anim;

	public CustomFrameLayout(Context context, int i) {
		super(context);
	}
	
	public CustomFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Trace
	@AfterViews
	protected void afterViews(){
	}
	
	@Click
	protected void title() {
	}

	@LongClick(R.id.title)
	protected void titleLongClick() {
	}

	@Touch(R.id.title)
	protected void titleTouched(MotionEvent e) {
	}

	@Background
	protected void someBackgroundTask(){
	}
	
	@UiThread
	protected void someUIThreadTask(){
	}

}
