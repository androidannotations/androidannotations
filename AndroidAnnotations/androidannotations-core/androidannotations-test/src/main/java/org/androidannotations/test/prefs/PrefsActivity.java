/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.test.prefs;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.test.R;

import android.app.Activity;

@EActivity(R.layout.main)
public class PrefsActivity extends Activity {

	@Pref
	SomePrefs_ somePrefs;

	@Pref
	ActivityDefaultPrefs_ activityDefaultPrefs;

	@Pref
	ActivityPrefs_ activityPrefs;

	@Pref
	ApplicationDefaultPrefs_ applicationDefaultPrefs;

	@Pref
	DefaultPrefs_ defaultPrefs;

	@Pref
	PublicPrefs_ publicPrefs;

	@Pref
	UniquePrefs_ uniquePrefs;

	@Pref
	InnerPrefs_.InnerSharedPrefs_ innerPrefs;

	SomePrefs_ methodInjectedPref;
	SomePrefs_ firstMultiInjectedPref;
	ActivityPrefs_ secondMultiInjectedPref;

	@Pref
	void methodInjectedPref(SomePrefs_ somePrefs) {
		methodInjectedPref = somePrefs;
	}

	void methodInjectedPref(@Pref SomePrefs_ somePrefs, @Pref ActivityPrefs_ activityPrefs) {
		firstMultiInjectedPref = somePrefs;
		secondMultiInjectedPref = activityPrefs;
	}
}
