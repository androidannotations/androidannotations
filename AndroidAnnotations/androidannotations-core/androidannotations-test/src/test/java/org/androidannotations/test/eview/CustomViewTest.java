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
package org.androidannotations.test.eview;

import static org.fest.assertions.api.Assertions.assertThat;

import org.androidannotations.test.EmptyActivityWithoutLayout_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;

@RunWith(RobolectricTestRunner.class)
public class CustomViewTest {

	@Test
	public void customViewWithoutAfterViewsDoesNotCallOnFinishInflate() {
		Context context = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		CustomView customView = CustomView_.build(context);
		assertThat(customView.onFinishInflateCalled).isFalse();
	}

	@Test
	public void customViewWithAfterViewsCallOnFinishInflate() {
		Context context = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		CustomViewWithAfterViews customView = CustomViewWithAfterViews_.build(context);
		assertThat(customView.isOnFinishInflateCall).isTrue();
	}

	@Test
	public void customViewWithViewsByIdInjectViewsAfterBuild() {
		Context context = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		CustomViewWithViewsById customView = CustomViewWithViewsById_.build(context);
		assertThat(customView.onFinishInflateCalled).isTrue();
	}

	@Test
	public void customViewWithViewByIdInjectViewAfterBuild() {
		Context context = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		CustomViewWithViewById customView = CustomViewWithViewById_.build(context);
		assertThat(customView.onFinishInflateCalled).isTrue();
	}

}
