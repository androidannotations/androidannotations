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
package org.androidannotations.test15.eviewgroup;

import static org.fest.assertions.api.Assertions.assertThat;

import org.androidannotations.test15.EmptyActivityWithoutLayout;
import org.androidannotations.test15.EmptyActivityWithoutLayout_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CustomFrameLayoutTest {

	@Test
	public void shouldHaveLayoutAfterCreate() {
		EmptyActivityWithoutLayout activity = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		CustomFrameLayout component = CustomFrameLayout_.build(activity, 0);
		assertThat(component.subtitle).isNotNull();
		assertThat(component.tv).isNotNull();
	}

}
