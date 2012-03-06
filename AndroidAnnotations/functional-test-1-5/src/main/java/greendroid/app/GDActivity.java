package greendroid.app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * This is a fake GreenDroid activity that has the same signature. Used to test
 * AndroidAnnotations integration with GreenDroid.
 */
public class GDActivity extends Activity {

	public void setActionBarContentView(int layoutResID) {
	}

	public void setActionBarContentView(View view) {
	}

	public void setActionBarContentView(View view, LayoutParams params) {
	}

}
