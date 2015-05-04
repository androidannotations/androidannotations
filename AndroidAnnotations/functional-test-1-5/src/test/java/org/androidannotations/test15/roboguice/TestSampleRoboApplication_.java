package org.androidannotations.test15.roboguice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.os.Handler;
import android.os.Message;
import org.androidannotations.api.UiThreadExecutor;
import org.robolectric.Robolectric;
import org.robolectric.TestLifecycleApplication;

import org.robolectric.shadows.ShadowHandler;
import roboguice.RoboGuice;
import android.app.Application;

// CHECKSTYLE:OFF
public class TestSampleRoboApplication_ extends Application implements TestLifecycleApplication {

	@Override
	public void onCreate() {
		super.onCreate();

		RoboGuice.overrideApplicationInjector(this, RoboGuice.newDefaultRoboModule(this), new RobolectricSampleModule());
		hackHandler();


	}

	//TODO remove this after upgrading robolectric to 3+
	private void hackHandler() {
		final Handler handler;
		try {
			Field handlerField = UiThreadExecutor.class.getDeclaredField("HANDLER");
			handlerField.setAccessible(true);
			handler = (Handler) handlerField.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		ShadowHandler shadowHandler = Robolectric.shadowOf_(handler);
		shadowHandler.__constructor__(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				//in the robolectric 2.4 there is a strange code - they call Handler's handleMessage (it do nothing)
				//instead of dispatch, that actually do the job. This is just a dirty work-around. It should be removed
				//with newer version of robolectric.
				handler.dispatchMessage(msg);
				return true;
			}
		});
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