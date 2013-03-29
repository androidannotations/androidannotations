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
package org.androidannotations.test15.greendroid;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.androidannotations.test15.R;

@RunWith(AndroidAnnotationsTestRunner.class)
public class MyGreenDroidActivityTest {
	
	@Test
	public void when_layout_defined_then_onCreate_calls_setActionBarContentView_with_layout_id_value() {
		MyGreenDroidActivity_ activity = new MyGreenDroidActivity_();
		activity.onCreate(null);
		assertThat(activity.layoutResID).isEqualTo(R.layout.main);
	}
	
	@Test
	public void afterViews_method_is_called_in_setActionBarContentView() {
		MyGreenDroidActivity_ activity = new MyGreenDroidActivity_();
		activity.onCreate(null);
		activity.setActionBarContentView(0);
		assertThat(activity.afterViewsCalled).isTrue();
	}

}
