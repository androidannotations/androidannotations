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
package org.androidannotations.generation;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.res.DrawableRes;

import android.app.Activity;
import android.graphics.drawable.Drawable;

@EActivity
public class ActivityWithGetDrawableMethod extends Activity {

	@DrawableRes(R.drawable.myDrawable)
	Drawable myDrawable;

	// http://developer.android.com/reference/android/content/Context.html#getDrawable(int)
	// This method was added in API 21 and should be used to get a Drawable which is styled/adjusted for the current theme.
	public Drawable getDrawable(int drawableId) {
		return null;
	}

}
