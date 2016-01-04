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
package org.androidannotations.test.eview;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

@EView
public class CustomViewWithAfterViews extends View {

	public boolean isOnFinishInflateCall;

	public CustomViewWithAfterViews(Context context) {
		super(context);
	}

	public CustomViewWithAfterViews(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomViewWithAfterViews(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		isOnFinishInflateCall = true;
	}

	@AfterViews
	void afterViews() {
	}
}
