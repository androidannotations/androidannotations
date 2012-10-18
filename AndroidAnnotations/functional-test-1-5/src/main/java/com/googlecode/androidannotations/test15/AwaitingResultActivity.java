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
import com.googlecode.androidannotations.annotations.OnActivityResult;

@EActivity(R.layout.views_injected)
public class AwaitingResultActivity extends Activity {

	@OnActivityResult(R.id.first_request)
	void onResult() {
	}

	@OnActivityResult(R.id.second_request)
	void onResultWithData(Intent intentData) {
	}

	@OnActivityResult
	void secondRequestResult(int result, Intent intentData) {
	}

	@OnActivityResult
	void secondRequestResult(Intent intentData, int result) {
	}

	@OnActivityResult(R.id.third_request)
	void onResultWithIntResultCode(int resultCode) {
	}

	@OnActivityResult(R.id.third_request)
	void onResultWithIntegerResultCode(Integer resultCodeInteger) {
	}

	@OnActivityResult({ R.id.first_request, R.id.second_request })
	void firstAndSecondRequestResult(Integer resultCodeInteger) {
	}

	@OnActivityResult(resName = { "third_request", "second_request" })
	void secondAndThirdRequestResult(Integer resultCodeInteger) {
	}

}
