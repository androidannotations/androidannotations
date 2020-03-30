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
package org.androidannotations.test.eview;

import static org.fest.assertions.api.Assertions.assertThat;

import org.androidannotations.test.EmptyActivityWithoutLayout_;
import org.androidannotations.test.instancestate.MyParcelableBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.os.Parcelable;

@RunWith(RobolectricTestRunner.class)
public class ViewInstanceStateTest {

	@Test
	public void viewInstanceStateSaveTest() {
		Context context = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		ViewWithInstanceState_ savingView = (ViewWithInstanceState_) ViewWithInstanceState_.build(context);

		assertThat(savingView.stateTest).isEqualTo("does it work?");
		assertThat(savingView.stringState).isEqualTo("does it work?");
		assertThat(savingView.beanState.getX()).isEqualTo(0);

		savingView.stateTest = "it works!";
		savingView.stringState = "it works!";
		savingView.beanState = new MyParcelableBean(1);
		Parcelable savedInstanceState = savingView.onSaveInstanceState();

		ViewWithInstanceState_ restoringView = (ViewWithInstanceState_) ViewWithInstanceState_.build(context);
		assertThat(restoringView.stateTest).isEqualTo("does it work?");
		assertThat(restoringView.stringState).isEqualTo("does it work?");
		assertThat(restoringView.beanState.getX()).isEqualTo(0);

		restoringView.onRestoreInstanceState(savedInstanceState);
		assertThat(restoringView.stateTest).isEqualTo("it works!");
		assertThat(restoringView.stringState).isEqualTo("it works!");
		assertThat(restoringView.beanState.getX()).isEqualTo(1);
	}
}
