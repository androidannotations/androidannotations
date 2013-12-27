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
package org.androidannotations.test15.res;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.res.Resources;
import android.text.Html;
import android.view.animation.AnimationUtils;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.androidannotations.test15.R;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowHtml;

@RunWith(AndroidAnnotationsTestRunner.class)
public class ResActivityTest {

	private ResActivity_ activity;

	@Before
	public void setup() {
		Robolectric.bindShadowClass(ShadowHtml.class);
		activity = new ResActivity_();
		activity.onCreate(null);
	}

	@Test
	public void string_snake_case_injected() {
		assertThat(activity.injected_string).isEqualTo("test");
	}

	@Test
	public void string_camel_case_injected() {
		assertThat(activity.injectedString).isEqualTo("test");
	}

	/**
	 * Cannot be tested right now, because there is no Robolectric shadow class
	 * for {@link AnimationUtils}.
	 */
	// @Test
	public void animNotNull() {
		assertThat(activity.fadein).isNotNull();
	}

	/**
	 * Cannot be tested right now, because the Robolectric shadow class for
	 * {@link Resources} doesn't implement {@link Resources#getAnimation(int)}
	 */
	// @Test
	public void xmlResAnimNotNull() {
		assertThat(activity.fade_in).isNotNull();
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
		assertEquals(Html.fromHtml(activity.getString(R.string.hello_html)), activity.helloHtml);
	}

	@Test
	public void htmlInjectedCorrectlySet() {
		assertEquals(Html.fromHtml(activity.getString(R.string.hello_html)), activity.htmlInjected);
	}
}
