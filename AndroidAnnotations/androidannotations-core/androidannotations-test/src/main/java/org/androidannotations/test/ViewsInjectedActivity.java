/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.test;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@EActivity(R.layout.views_injected)
public class ViewsInjectedActivity extends Activity {

	int counter;

	View unboundView;

	@ViewById
	Button myButton;

	@ViewById(R.id.my_text_view)
	TextView someView;

	@ViewById
	TextView myTextView;

	@ViewsById({ R.id.my_text_view, R.id.myButton })
	List<View> views;

	@ViewsById({ R.id.my_text_view, R.id.someView })
	List<TextView> textViews;

	TextView methodInjectedView;
	TextView multiInjectedView;
	List<TextView> methodInjectedViews;
	List<View> multiInjectedViews;

	@AfterViews
	void incrementCounter() {
		counter++;
	}

	@ViewById(R.id.my_text_view)
	void methodInjectedView(TextView someView) {
		methodInjectedView = someView;
	}

	void multiInjectedView(@ViewById TextView someView, @ViewById(R.id.my_text_view) TextView activityPrefs) {
		multiInjectedView = someView;
	}

	@ViewsById({ R.id.my_text_view, R.id.someView })
	void methodInjectedViews(List<TextView> someView) {
		methodInjectedViews = someView;
	}

	void multiInjectedViews(@ViewsById({ R.id.someView, R.id.myButton }) List<View> someView, @ViewsById(R.id.my_text_view) List<TextView> activityPrefs) {
		multiInjectedViews = someView;
	}

}
