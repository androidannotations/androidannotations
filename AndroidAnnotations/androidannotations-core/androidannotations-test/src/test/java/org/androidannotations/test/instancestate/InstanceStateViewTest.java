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
package org.androidannotations.test.instancestate;

import static org.fest.assertions.api.Assertions.assertThat;

import org.androidannotations.test.EmptyActivityWithoutLayout_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.os.Bundle;

@RunWith(RobolectricTestRunner.class)
public class InstanceStateViewTest {

	@Test
	public void testInstanceState() {
		int restoredValue = 42;

		Bundle bundle = new Bundle();
		bundle.putInt("instanceField", restoredValue);

		Context context = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		InstanceStateView stateView = InstanceStateView_.build(context);

		assertThat(stateView.instanceField).isEqualTo(-1);
		assertThat(stateView.restoredInRestoreInstanceState).isEqualTo(-2);

		stateView.onRestoreInstanceState(bundle);

		assertThat(stateView.instanceField).isEqualTo(restoredValue);
		assertThat(stateView.restoredInRestoreInstanceState).isEqualTo(restoredValue);
	}
}
