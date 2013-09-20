package org.androidannotations.test15.efragment;

import android.support.v4.app.Fragment;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.test15.ExtraInjectedActivity_;

@EFragment
public class StartActivityFragmentSupport extends Fragment {
	void startActivity() {
		ExtraInjectedActivity_.intent(this).start();
	}
}
