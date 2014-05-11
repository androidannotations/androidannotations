package org.androidannotations.api.builder;

import android.content.ComponentName;
import android.content.Context;

public class ServiceIntentBuilder<I extends ServiceIntentBuilder<I>> extends IntentBuilder<I> {

	public ServiceIntentBuilder(Context context, Class<?> clazz) {
		super(context, clazz);
	}

	public ComponentName start() {
		return context.startService(intent);
	}

	public boolean stop() {
		return context.stopService(intent);
	}
}
