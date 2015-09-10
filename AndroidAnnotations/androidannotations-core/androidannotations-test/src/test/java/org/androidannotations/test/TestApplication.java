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
package org.androidannotations.test;

import static org.fest.reflect.core.Reflection.staticField;

import java.lang.reflect.Method;

import org.androidannotations.api.UiThreadExecutor;
import org.robolectric.Robolectric;
import org.robolectric.TestLifecycleApplication;
import org.robolectric.shadows.ShadowHandler;

import android.app.Application;
import android.os.Handler;
import android.os.Message;

public class TestApplication extends Application implements TestLifecycleApplication {

	@Override
	public void beforeTest(Method method) {
	}

	@Override
	public void prepareTest(Object test) {
		hackHandler();
	}

	@Override
	public void afterTest(Method method) {
	}

	// TODO remove this after upgrading robolectric to 3+
	private void hackHandler() {
		final Handler handler = staticField("HANDLER").ofType(Handler.class).in(UiThreadExecutor.class).get();
		ShadowHandler shadowHandler = Robolectric.shadowOf_(handler);
		shadowHandler.__constructor__(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				// in the robolectric 2.4 there is a strange code - they call
				// Handler's handleMessage (it do nothing)
				// instead of dispatch, that actually do the job. This is just a
				// dirty work-around. It should be removed
				// with newer version of robolectric.
				handler.dispatchMessage(msg);
				return true;
			}
		});
	}
}
