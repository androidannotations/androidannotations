package org.androidannotations.api.support.app;

import android.app.IntentService;
import android.content.Intent;

/**
 * Convenience class for
 * {@link org.androidannotations.annotations.EIntentService EIntentService}s.
 * This adds an empty implementation of
 * {@link IntentService#onHandleIntent(Intent) onHandleIntent}, so you do not
 * have to in your actual enhanced class.
 */
public abstract class AbstractIntentService extends IntentService {

	public AbstractIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

	}

}
