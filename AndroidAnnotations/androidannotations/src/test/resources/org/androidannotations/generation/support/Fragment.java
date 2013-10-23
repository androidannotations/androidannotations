package android.support.v4.app;

import android.content.Intent;
import android.app.Activity;

/**
 * We have to put this on resources folder because we want to add it to
 * classpath only on some unit tests methods
 */
public class Fragment {

	public Activity getActivity() {
		return null;
	}

	public void startActivityForResult(Intent intent, int flag) {

	}

}
