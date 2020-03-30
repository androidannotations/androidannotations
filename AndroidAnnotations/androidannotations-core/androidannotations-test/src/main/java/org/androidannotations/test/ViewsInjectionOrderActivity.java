/**
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

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewsById;

import android.app.Activity;
import android.widget.TextView;

@EActivity(R.layout.views_injected)
public class ViewsInjectionOrderActivity extends Activity {

	List<TextView> methodInjectedViews;

	@ViewsById({ R.id.my_text_view, R.id.someView })
	void methodInjectedViews(List<TextView> someView) {
		methodInjectedViews = someView;
	}
}
