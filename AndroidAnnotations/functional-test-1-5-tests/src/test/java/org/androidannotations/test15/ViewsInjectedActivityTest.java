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

//import static org.fest.assertions.Assertions.assertThat;
import static org.androidannotations.test15.MyAssertions.assertThat;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidAnnotationsTestRunner.class)
public class ViewsInjectedActivityTest {

	private ViewsInjectedActivity_ activity;

	@Before
	public void setup() {
		activity = new ViewsInjectedActivity_();
		activity.onCreate(null);
	}

	@Test
	public void injectionWithConventionIsDone() {
		assertThat(activity.myButton).hasId(R.id.myButton);
	}

	@Test
	public void injectionWithConfigurationOverridesConvention() {
		assertThat(activity.someView).hasId(R.id.my_text_view);
	}

	@Test
	public void multipleInjectionIsSame() {
		assertThat(activity.someView).isSameAs(activity.myTextView);
	}

	@Test
	public void unannotatedViewIsNull() {
		assertThat(activity.unboundView).isNull();
	}

	@Test
	public void countAfterSetContentViewCalls() {
		Assertions.assertThat(activity.counter).isEqualTo(1);
		activity.setContentView(R.layout.views_injected);
		Assertions.assertThat(activity.counter).isEqualTo(2);
	}

}
