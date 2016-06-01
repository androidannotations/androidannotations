/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.viewlistener;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.Touch;
import org.androidannotations.viewbyid.R;

import android.app.Activity;

@EActivity
public abstract class AbstractViewListenerActivity extends Activity {

	@FocusChange(R.id.view)
	protected void onFocusChange() {
	}

	@Click(R.id.view)
	protected void onClick() {
	}

	@LongClick(R.id.view)
	protected void onLongClick() {
	}

	@Touch(R.id.view)
	protected void onTouch() {
	}
}
