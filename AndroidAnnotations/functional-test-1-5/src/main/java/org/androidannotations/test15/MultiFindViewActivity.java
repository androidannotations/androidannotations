/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
import org.androidannotations.annotations.*;

@EActivity(R.layout.views_injected)
public class MultiFindViewActivity extends Activity {

	@ViewById(R.id.my_text_view)
	View view;

	@ViewById(R.id.my_text_view)
	TextView textView;

	@ViewById(R.id.myButton)
	Button button;

	@Click({ R.id.my_text_view, R.id.myButton})
	void viewClicked() {

	}

	@LongClick({ R.id.myButton, R.id.someView })
	void viewLongClicked() {

	}

	@FocusChange(R.id.my_text_view)
	void textViewFocusChanged() {

	}

	@Click(R.id.someView)
	void someViewClicked() {

	}

}
