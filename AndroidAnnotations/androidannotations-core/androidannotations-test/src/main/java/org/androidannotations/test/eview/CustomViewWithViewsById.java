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

import java.util.List;

import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.ViewsById;
import org.androidannotations.test.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

@EView
public class CustomViewWithViewsById extends View {

	@ViewsById({ R.id.title, R.id.subtitle })
	List<TextView> views;

	boolean onFinishInflateCalled;

	public CustomViewWithViewsById(Context context) {
		super(context);
	}

	public CustomViewWithViewsById(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomViewWithViewsById(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		onFinishInflateCalled = true;
	}
}
