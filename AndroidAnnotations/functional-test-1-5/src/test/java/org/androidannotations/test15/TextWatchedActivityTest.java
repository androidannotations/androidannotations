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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.setupActivity;
import static org.robolectric.Robolectric.shadowOf_;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowTextView;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.widget.TextView;

@RunWith(RobolectricTestRunner.class)
public class TextWatchedActivityTest {

	private TextWatchedActivity_ activity;

	@Before
	public void setUp() {
		activity = setupActivity(TextWatchedActivity_.class);
	}

	@Test
	public void testAfterTextChangeHandled() {
		assertThat(activity.afterTextChangeHandled).isFalse();

		TextView textView = (TextView) activity.findViewById(R.id.helloTextView);

		afterTextChanged(textView, null);

		assertThat(activity.afterTextChangeHandled).isTrue();
	}

	@Test
	public void testBeforeTextChangeHandled() {
		assertThat(activity.beforeTextChangeHandled).isFalse();

		TextView textView = (TextView) activity.findViewById(R.id.helloTextView);

		beforeTextChanged(textView, null, 0, 0, 0);

		assertThat(activity.beforeTextChangeHandled).isTrue();
	}

	@Test
	public void testOnTextChangeHandled() {
		assertThat(activity.onTextChangeHandled).isFalse();

		TextView textView = (TextView) activity.findViewById(R.id.watchedEditText);

		onTextChanged(textView, null, 0, 0, 0);

		assertThat(activity.onTextChangeHandled).isTrue();
	}

	@Test
	public void testAfterTextChangeTextViewPassed() {
		assertThat(activity.afterTextView).isNull();

		TextView textView = (TextView) activity.findViewById(R.id.helloTextView);

		afterTextChanged(textView, null);

		assertThat(activity.afterTextView).isEqualTo(textView);
	}

	@Test
	public void testAfterTextChangeEditablePassed() {
		assertThat(activity.afterEditable).isNull();

		TextView textView = (TextView) activity.findViewById(R.id.helloTextView);
		Editable s = new SpannableStringBuilder("hello");

		afterTextChanged(textView, s);

		assertThat(activity.afterEditable).isEqualTo(s);
	}

	@Test
	public void testOnTextChangeTextViewPassed() {
		assertThat(activity.onTextView).isNull();

		TextView textView = (TextView) activity.findViewById(R.id.watchedEditText);

		onTextChanged(textView, null, 0, 0, 0);

		assertThat(activity.onTextView).isEqualTo(textView);
	}

	@Test
	public void testOnTextChangeParametersPassed() {
		assertThat(activity.onSequence).isNull();
		assertThat(activity.onStart).isZero();
		assertThat(activity.onBefore).isZero();
		assertThat(activity.onCount).isZero();

		TextView textView = (TextView) activity.findViewById(R.id.helloTextView);

		CharSequence s = "helloOnTextChange";
		int start = 1;
		int before = 2;
		int count = 3;

		onTextChanged(textView, s, start, before, count);

		assertThat(activity.onSequence).isEqualTo(s);
		assertThat(activity.onStart).isEqualTo(start);
		assertThat(activity.onBefore).isEqualTo(before);
		assertThat(activity.onCount).isEqualTo(count);
	}

	@Test
	public void testBeforeTextChangeTextViewPassed() {
		assertThat(activity.beforeTextView).isNull();

		TextView textView = (TextView) activity.findViewById(R.id.helloTextView);

		beforeTextChanged(textView, null, 0, 0, 0);

		assertThat(activity.beforeTextView).isEqualTo(textView);
	}

	@Test
	public void testBeforeTextChangeParametersPassedPassed() {
		assertThat(activity.beforeTextView).isNull();

		TextView textView = (TextView) activity.findViewById(R.id.helloTextView);

		beforeTextChanged(textView, null, 0, 0, 0);

		assertThat(activity.beforeTextView).isEqualTo(textView);
	}

	@Test
	public void testBeforeTextChangeParametersPassed() {
		assertThat(activity.beforeSequence).isNull();
		assertThat(activity.beforeStart).isZero();
		assertThat(activity.beforeAfter).isZero();
		assertThat(activity.beforeCount).isZero();

		TextView textView = (TextView) activity.findViewById(R.id.helloTextView);

		CharSequence s = "helloBeforeTextChange";
		int start = 1;
		int after = 2;
		int count = 3;

		beforeTextChanged(textView, s, start, count, after);

		assertThat(activity.beforeSequence).isEqualTo(s);
		assertThat(activity.beforeStart).isEqualTo(start);
		assertThat(activity.beforeAfter).isEqualTo(after);
		assertThat(activity.beforeCount).isEqualTo(count);
	}

	private static void afterTextChanged(TextView textView, Editable s) {
		ShadowTextView shadowTextView = shadowOf_(textView);
		for (TextWatcher textWatcher : shadowTextView.getWatchers()) {
			textWatcher.afterTextChanged(s);
		}
	}

	private static void beforeTextChanged(TextView textView, CharSequence s, int start, int count, int after) {
		ShadowTextView shadowTextView = shadowOf_(textView);
		for (TextWatcher textWatcher : shadowTextView.getWatchers()) {
			textWatcher.beforeTextChanged(s, start, count, after);
		}
	}

	private static void onTextChanged(TextView textView, CharSequence s, int start, int before, int count) {
		ShadowTextView shadowTextView = shadowOf_(textView);
		for (TextWatcher textWatcher : shadowTextView.getWatchers()) {
			textWatcher.onTextChanged(s, start, before, count);
		}
	}
}
