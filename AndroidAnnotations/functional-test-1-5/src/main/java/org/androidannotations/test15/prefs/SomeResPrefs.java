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
package org.androidannotations.test15.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultRes;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.annotations.sharedpreferences.SharedPref.Scope;
import org.androidannotations.test15.R;

@SharedPref(Scope.UNIQUE)
public interface SomeResPrefs {

	@DefaultRes
	String prefDefaultString();

	@DefaultRes(R.string.prefDefaultString)
	String nameResId();

	@DefaultRes(resName = "prefDefaultString")
	String nameResName();

	@DefaultRes
	int prefDefaultInt();

	@DefaultRes(R.integer.prefDefaultInt)
	int ageResId();

	@DefaultRes
	long prefDefaultLong();

	@DefaultRes(R.integer.prefDefaultLong)
	long ageLongResId();

	@DefaultRes
	float prefDefaultFloat();

	@DefaultRes(R.integer.prefDefaultFloat)
	float ageFloatResId();

	@DefaultRes
	boolean prefsDefaultBool();

	@DefaultRes(R.bool.prefsDefaultBool)
	boolean isAwesomeResId();

}
