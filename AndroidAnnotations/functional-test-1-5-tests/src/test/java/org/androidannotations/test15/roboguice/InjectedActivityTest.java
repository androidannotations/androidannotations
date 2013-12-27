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
package org.androidannotations.test15.roboguice;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.widget.TextView;

import com.google.inject.Inject;
import org.androidannotations.test15.R;

@RunWith(InjectedTestRunner.class)
public class InjectedActivityTest {

	@Inject
	Context context;

	@Inject
	ActivityWithRoboGuice_ injectedActivity;

	@Inject
	Counter fieldCounter;
	@Inject
	FakeDateProvider fakeDateProvider;

	@Before
	public void setUp() {
		fakeDateProvider.setDate("December 8, 2010");
	}

	@Test
	public void shouldAssignStringToTextView() throws Exception {
		injectedActivity.onCreate(null);
		TextView injectedTextView = (TextView) injectedActivity.findViewById(R.id.injected_text_view);
		assertThat(injectedTextView.getText().toString(), equalTo("Roboguice Activity tested with Robolectric - December 8, 2010"));
	}

	@Test
	public void shouldInjectSingletons() throws Exception {
		Counter instance = injectedActivity.getInjector().getInstance(Counter.class);
		assertEquals(0, instance.count);

		instance.count++;

		Counter instanceAgain = injectedActivity.getInjector().getInstance(Counter.class);
		assertEquals(1, instanceAgain.count);

		assertSame(fieldCounter, instance);
	}

	@Test
	public void shouldBeAbleToInjectAContext() throws Exception {
		assertNotNull(context);
	}

}
