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

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.component)
public class ViewGroup extends FrameLayout {

	@ViewById(R.id.title)
	TextView tv;

	@ViewById
	TextView subtitle;

	public ViewGroup(Context context, int i) {
		super(context);
	}
	
	public ViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Click(R.id.title)
	public void title() {
	}

	@LongClick(R.id.title)
	public void titleLongClick() {
	}

//	@Touch(R.id.title)
//	public void titleTouched(MotionEvent e) {
//		
//	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

}
