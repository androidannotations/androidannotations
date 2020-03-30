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
package org.androidannotations.test.res;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.androidannotations.test.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.text.Html;

@RunWith(RobolectricTestRunner.class)
public class ResActivityTest {

	private ResActivity_ activity;

	@Before
	public void setUp() {
		activity = Robolectric.buildActivity(ResActivity_.class).create().get();
	}

	@Test
	public void stringSnakeCaseInjected() {
		assertThat(activity.injected_string).isEqualTo("test");
	}

	@Test
	public void stringCamelCaseInjected() {
		assertThat(activity.injectedString).isEqualTo("test");
	}

	@Test
	public void methodInjectedStringNotNull() {
		assertNotNull(activity.methodInjectedString);
	}

	@Test
	public void multiInjectedStringNotNull() {
		assertNotNull(activity.multiInjectedString);
	}

	@Test
	public void animNotNull() {
		assertThat(activity.fadein).isNotNull();
	}

	/**
	 * Cannot be tested right now, because the Robolectric shadow class for
	 * {@link android.content.res.Resources Resources} doesn't implement
	 * {@link android.content.res.Resources#getAnimation(int)
	 * Resources#getAnimation(int)}
	 */
	// @Test
	public void xmlResAnimNotNull() {
		assertThat(activity.fade_in).isNotNull();
	}

	@Test
	public void methodInjectedAnimationNotNull() {
		assertNotNull(activity.methodInjectedAnimation);
	}

	@Test
	public void multiInjectedAnimationNotNull() {
		assertNotNull(activity.multiInjectedAnimation);
	}

	@Test
	public void drawableResNotNull() {
		assertNotNull(activity.icon);
	}

	@Test
	public void methodInjectedDrawableNotNull() {
		assertNotNull(activity.methodInjectedDrawable);
	}

	@Test
	public void multiInjectedDrawableNotNull() {
		assertNotNull(activity.multiInjectedDrawable);
	}

	@Test
	public void htmlResNotNull() {
		assertNotNull(activity.helloHtml);
	}

	@Test
	public void htmlInjectedNotNull() {
		assertNotNull(activity.htmlInjected);
	}

	@Test
	public void htmlResCorrectlySet() {
		assertEquals(Html.fromHtml(activity.getString(R.string.hello_html)).toString(), activity.helloHtml.toString());
	}

	@Test
	public void htmlInjectedCorrectlySet() {
		assertEquals(Html.fromHtml(activity.getString(R.string.hello_html)).toString(), activity.htmlInjected.toString());
	}

	@Test
	public void methodInjectedHtmlNotNull() {
		assertNotNull(activity.methodInjectedHtml);
	}

	@Test
	public void multiInjectedHtmlNotNull() {
		assertNotNull(activity.multiInjectedHtml);
	}

}
