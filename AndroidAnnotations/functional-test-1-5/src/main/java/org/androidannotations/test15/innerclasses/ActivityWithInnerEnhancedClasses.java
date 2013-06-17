package org.androidannotations.test15.innerclasses;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.androidannotations.annotations.*;
import org.androidannotations.test15.R;

@EActivity(R.layout.views_injected)
public class ActivityWithInnerEnhancedClasses extends Activity {

	@ViewById(R.id.someView)
	InnerViewGroup innerViewGroup;

	@EViewGroup(R.layout.injected)
	public static class InnerViewGroup extends LinearLayout {

		@ViewById(R.id.injected_text_view)
		TextView textView;


		public InnerViewGroup(Context context) {
			super(context);
		}
	}

	@EBean
	public static class InnerBean {

	}

	@EFragment(R.layout.component)
	public static class InnerFragment extends Fragment {

	}
}
