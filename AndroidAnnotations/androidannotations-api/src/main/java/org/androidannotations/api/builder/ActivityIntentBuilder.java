package org.androidannotations.api.builder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityIntentBuilder<I extends ActivityIntentBuilder<I>> extends IntentBuilder<I> {

	public ActivityIntentBuilder(Context context, Class<?> clazz) {
		super(context, clazz);
	}

	public ActivityIntentBuilder(Context context, Intent intent) {
		super(context, intent);
	}

	public void start() {
		context.startActivity(intent);
	}

	public void startForResult(int requestCode) {
		if (context instanceof Activity) {
			((Activity) context).startActivityForResult(intent, requestCode);
		} else {
			context.startActivity(intent);
		}
	}
}
