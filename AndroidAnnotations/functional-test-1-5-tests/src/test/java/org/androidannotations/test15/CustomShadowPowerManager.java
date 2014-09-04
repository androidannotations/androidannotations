package org.androidannotations.test15;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowPowerManager;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

@Implements(PowerManager.class)
public class CustomShadowPowerManager extends ShadowPowerManager {

	public static int lastFlags;
	public static String lastTag;
	
	@Implementation
	@Override
	public WakeLock newWakeLock(int flags, String tag) {
		lastFlags = flags;
		lastTag = tag;
		return super.newWakeLock(flags, tag);
	}
}
