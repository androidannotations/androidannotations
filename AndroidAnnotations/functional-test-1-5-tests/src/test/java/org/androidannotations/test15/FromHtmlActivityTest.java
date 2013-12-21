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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.text.Html;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowHtml;

@RunWith(AndroidAnnotationsTestRunner.class)
public class FromHtmlActivityTest {

	private FromHtmlActivity_ activity;

	@Before
	public void setup() {
		Robolectric.bindShadowClass(ShadowHtml.class);

		activity = new FromHtmlActivity_();
		activity.onCreate(null);
	}

	@Test
	public void injectionOfHtmlTest() {
		assertNotNull(activity.textView);
		assertEquals(Html.fromHtml(activity.getString(R.string.hello_html)),
				activity.textView.getText());
	}

	@Test
	public void injectionOfHtmlWithDefaultName() {
		assertNotNull(activity.someView);
		assertEquals(Html.fromHtml(activity.getString(R.string.someView)),
				activity.someView.getText());
	}
}
