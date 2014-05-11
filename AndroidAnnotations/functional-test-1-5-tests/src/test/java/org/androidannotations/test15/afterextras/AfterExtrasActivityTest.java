/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.afterextras;

import static org.fest.assertions.Assertions.assertThat;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

@RunWith(AndroidAnnotationsTestRunner.class)
public class AfterExtrasActivityTest {

	AfterExtrasActivity_ activity;
	Intent intent;

	@Before
	public void setup() {
		activity = new AfterExtrasActivity_();
		intent = AfterExtrasActivity_.intent(activity).extraData(true).get();
	}

	@Test
	public void afterExtra_called_activity_after_setIntent() {
		activity.setIntent(intent);

		assertThat(activity.extraDataSet).isTrue();
		assertThat(activity.afterExtrasCalled).isTrue();
	}

}
