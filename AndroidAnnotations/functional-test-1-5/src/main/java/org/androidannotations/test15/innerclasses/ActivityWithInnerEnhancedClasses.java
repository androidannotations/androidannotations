/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test15.innerclasses;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.test15.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	protected static class InnerBean {

	}

	@EFragment(R.layout.component)
	public static class InnerFragment extends Fragment {

	}
}
