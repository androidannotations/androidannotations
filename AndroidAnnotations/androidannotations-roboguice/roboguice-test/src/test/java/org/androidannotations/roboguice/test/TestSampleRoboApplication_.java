package org.androidannotations.roboguice.test;

import java.lang.reflect.Method;

import org.robolectric.Robolectric;
import org.robolectric.TestLifecycleApplication;

import android.app.Application;

import roboguice.RoboGuice;

// CHECKSTYLE:OFF
public class TestSampleRoboApplication_ extends Application implements TestLifecycleApplication {
	// CHECKSTYLE:ON
	@Override
	public void onCreate() {
		super.onCreate();

		RoboGuice.overrideApplicationInjector(this, RoboGuice.newDefaultRoboModule(this), new RobolectricSampleModule());
	}

	@Override
	public void beforeTest(Method method) {
	}

	@Override
	public void prepareTest(Object test) {
		TestSampleRoboApplication_ application = (TestSampleRoboApplication_) Robolectric.application;

		RoboGuice.overrideApplicationInjector(application, RoboGuice.newDefaultRoboModule(application), new RobolectricSampleTestModule());

		RoboGuice.getInjector(application).injectMembers(test);
	}

	@Override
	public void afterTest(Method method) {
	}
}
