/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.test.menu;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.view.Menu;

@RunWith(RobolectricTestRunner.class)
public class InjectMenuActivityTest {

	private InjectMenuActivity_ injectMenuActivity;

	@Before
	public void setUp() {
		injectMenuActivity = Robolectric.buildActivity(InjectMenuActivity_.class).create().get();
		injectMenuActivity.mockMenuInflater = createMenuInflater();
	}

	@Test
	public void menuIsNull() {
		assertThat(injectMenuActivity.menu).isNull();
	}

	@Test
	public void testMenuInjectedFromOnCreateOptionsMenu() {
		Menu menu = mock(Menu.class);
		injectMenuActivity.onCreateOptionsMenu(menu);
		assertThat(injectMenuActivity.menu).isSameAs(menu);
	}

	@Test
	public void methodInjectionComesAfterInflation() {
		Menu menu = mock(Menu.class);
		assertThat(injectMenuActivity.menuIsInflated).isFalse();
		injectMenuActivity.onCreateOptionsMenu(menu);
		assertThat(injectMenuActivity.menuIsInflated).isTrue();
	}

	@Test
	public void methodInjectedMenu() {
		Menu menu = mock(Menu.class);
		injectMenuActivity.onCreateOptionsMenu(menu);
		assertThat(injectMenuActivity.methodInjectedMenu).isSameAs(menu);
	}

	@Test
	public void multiInjectedMenu() {
		Menu menu = mock(Menu.class);
		injectMenuActivity.onCreateOptionsMenu(menu);
		assertThat(injectMenuActivity.multiInjectedMenu).isSameAs(menu);
	}

	private InjectMenuActivity.MockMenuInflater createMenuInflater() {
		final InjectMenuActivity.MockMenuInflater menuInflater = mock(InjectMenuActivity.MockMenuInflater.class);
		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				menuInflater.menuInflated = true;
				return null;
			}
		}).when(menuInflater).inflate(anyInt(), any(Menu.class));
		return menuInflater;
	}

}
