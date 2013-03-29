/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
import android.text.Editable;
import android.widget.TextView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.BeforeTextChange;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;

@EActivity(R.layout.main)
public class TextWatchedActivity extends Activity {

	@AfterTextChange(R.id.helloTextView)
	void m1(Editable s) {}

	@AfterTextChange({R.id.helloTextView, R.id.watchedEditText})
	void m2(TextView tv, Editable s) {}

	@TextChange(R.id.watchedEditText)
	void m3(TextView editText, CharSequence s, int before) {}

	@TextChange(R.id.watchedEditText)
	void m4(TextView editText, CharSequence s, int before) {}

	@AfterTextChange(R.id.watchedEditText)
	void m5(Editable s) {}

	@AfterTextChange(R.id.watchedEditText)
	void m6(TextView editText, Editable s) {}

	@AfterTextChange(R.id.watchedEditText)
	void m7(Editable editable, TextView editText) {}

	@TextChange(R.id.helloTextView)
	void m8(CharSequence s, int before, int start, int count) {}

	@TextChange(R.id.helloTextView)
	void m9(CharSequence s, int start, int before, int count) {}

	@TextChange(R.id.helloTextView)
	void m10(CharSequence s, int count, int start, int before) {}

	@BeforeTextChange(R.id.helloTextView)
	void m11(CharSequence s) {}

	@BeforeTextChange(R.id.helloTextView)
	void m12(CharSequence s, int count) {}

	@BeforeTextChange(R.id.helloTextView)
	void m14(CharSequence s, int after, int start, int count) {}

	@BeforeTextChange(R.id.helloTextView)
	void m15(TextView tv, CharSequence s, int start, int after, int count) {}

	@BeforeTextChange(R.id.helloTextView)
	void m16(CharSequence s, int count, int start, int after) {}

	@BeforeTextChange
	void helloTextViewBeforeTextChanged() {}

	@AfterTextChange
	void helloTextViewAfterTextChanged() {}

	@TextChange
	void helloTextViewTextChanged() {}

}
