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
package org.androidannotations.test15.afterinject;

import android.app.Activity;
import android.app.NotificationManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;

@EActivity
public class AfterInjectActivity extends Activity {
	
	public boolean afterInjectCalled = false;
	public boolean notificationManagerNullAfterInject = true;

	@SystemService
	NotificationManager notificationManager;

	@AfterInject
	void afterInject() {
		afterInjectCalled = true;
		notificationManagerNullAfterInject = notificationManager == null;
	}

}
