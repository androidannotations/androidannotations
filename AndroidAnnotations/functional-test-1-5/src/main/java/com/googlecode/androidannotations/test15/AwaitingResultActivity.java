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
package com.googlecode.androidannotations.test15;

import android.app.Activity;
import android.content.Intent;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OnResult;

@EActivity(R.layout.views_injected)
public class AwaitingResultActivity extends Activity {

	private static final int REQUEST_CODE = 2;

	private static final int ANOTHER_REQUEST_CODE = 3;

	@OnResult(REQUEST_CODE)
	void onResult() {
	}

	@OnResult(ANOTHER_REQUEST_CODE)
	void onResultWithData(Intent intent) {
	}

	@OnResult(REQUEST_CODE)
	void onResultWithIntResultCode(int resultCode) {
	}

	@OnResult(REQUEST_CODE)
	void onResultWithIntegerResultCode(Integer resultCode) {
	}

	@OnResult(ANOTHER_REQUEST_CODE)
	void onResultWithResultCodeAndData(int result, Intent intent) {
	}

}
