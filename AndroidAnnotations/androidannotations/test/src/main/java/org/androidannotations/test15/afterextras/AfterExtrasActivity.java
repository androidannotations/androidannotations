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
package org.androidannotations.test15.afterextras;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.test15.R;

import android.app.Activity;

@EActivity(R.layout.main)
public class AfterExtrasActivity extends Activity {

	public static final String EXTRA_DATA_KEY = "EXTRA_DATA";

	@Extra(EXTRA_DATA_KEY)
	public boolean extraDataSet = false;

	public boolean afterExtrasCalled = false;

	@AfterExtras
	void afterExtras() {
		afterExtrasCalled = true;
	}

}
