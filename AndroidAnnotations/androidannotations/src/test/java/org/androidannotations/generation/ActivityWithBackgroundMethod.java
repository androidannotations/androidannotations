package org.androidannotations.generation;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;

@EActivity
public class ActivityWithBackgroundMethod extends Activity {

	@Background
	public void executingOnBackground() {

	}

}
