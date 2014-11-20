package org.androidannotations.api.support.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Convenience class for
 * {@link org.androidannotations.annotations.ReceiverAction ReceiverAction}. If
 * you extend from it, this adds an empty implementation of
 * {@link BroadcastReceiver#onReceive(Context, Intent) onReceive}, so you do not
 * have to do in your actual class.
 */
public abstract class AbstractBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

	}

}
