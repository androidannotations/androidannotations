package org.androidannotations.api.builder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ServiceIntentBuilder<I extends ServiceIntentBuilder<I>> extends IntentBuilder<I> {

	public ServiceIntentBuilder(Context context, Class<?> clazz) {
		super(context, clazz);
	}

	public ServiceIntentBuilder(Context context, Intent intent) {
		super(context, intent);
	}

	public ComponentName start() {
		return context.startService(intent);
	}

	public boolean stop() {
		return context.stopService(intent);
	}
}
