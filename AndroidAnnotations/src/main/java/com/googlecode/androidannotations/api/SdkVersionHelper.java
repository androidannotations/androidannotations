package com.googlecode.androidannotations.api;

import android.os.Build;
import android.os.Build.VERSION;

public class SdkVersionHelper {

	public static int getSdkInt() {
		if (Build.VERSION.RELEASE.startsWith("1.5"))
			return 3;

		return HelperInternal.getSdkIntInternal();
	}

	private static class HelperInternal {
		private static int getSdkIntInternal() {
			return VERSION.SDK_INT;
		}
	}

}
