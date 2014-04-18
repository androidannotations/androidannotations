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
package org.androidannotations.test15.instancestate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.Bundle;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.CustomShadowBundle;

@RunWith(AndroidAnnotationsTestRunner.class)
public class SaveInstanceStateActivityTest {

	@Before
	public void setup() throws Exception {
		Robolectric.bindShadowClass(CustomShadowBundle.class);
	}

	@Test
	public void can_create_with_empty_bundle() {
		SaveInstanceStateActivity_ activity = new SaveInstanceStateActivity_();
		Bundle emptyBundle = new Bundle();
		activity.onCreate(emptyBundle);
	}

	@Test
	public void can_create_without_saved_state() {
		SaveInstanceStateActivity_ activity = new SaveInstanceStateActivity_();
		activity.onCreate(null);
	}

}
