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
package org.androidannotations.test15;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.util.List;

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

	@ViewsById({R.id.my_text_view, R.id.myButton})
	List<View> views;

	@ViewsById({R.id.my_text_view, R.id.someView})
	List<TextView> textViews;

	@AfterViews
	void incrementCounter() {
		counter++;
	}

}
