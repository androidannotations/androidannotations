/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.eintentservice;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.test15.ebean.EnhancedClass;

import android.app.IntentService;
import android.content.Intent;

@EIntentService
public class MyIntentService extends IntentService {

	@Bean
	EnhancedClass dependency;

	public MyIntentService() {
		super(MyIntentService.class.getSimpleName());
	}

	@ServiceAction
	void myAction() {

	}

	@ServiceAction
	void actionOne(String valueString) {

	}

	@ServiceAction("myAction")
	void actionThree(String valueString, long valueLong) {

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Do nothing here
	}

}
