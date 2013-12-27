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
package org.androidannotations.test15.roboguice;

import org.junit.runners.model.InitializationError;

import roboguice.inject.ContextScope;
import android.app.Application;

import com.google.inject.Injector;
import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import com.xtremelabs.robolectric.Robolectric;

public class InjectedTestRunner extends AndroidAnnotationsTestRunner {

	public InjectedTestRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

	@Override
	protected Application createApplication() {
		SampleRoboApplication application = (SampleRoboApplication) super.createApplication();
		application.setModule(new RobolectricSampleTestModule());
		return application;
	}

	@Override
	public void prepareTest(Object test) {
		SampleRoboApplication application = (SampleRoboApplication) Robolectric.application;

		// This project's application does not extend GuiceInjectableApplication
		// therefore we need to enter the ContextScope manually.
		Injector injector = application.getInjector();
		ContextScope scope = injector.getInstance(ContextScope.class);
		scope.enter(application);

		injector.injectMembers(test);
	}
}
