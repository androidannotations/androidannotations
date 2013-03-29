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
package org.androidannotations.test15.inheritance;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Context;

@RunWith(AndroidAnnotationsTestRunner.class)
public class InheritanceTest {

	@Test
	public void after_inject_mother_calls_first() {
		Child child = Child_.getInstance_(mock(Context.class));
		assertThat(child.motherInitWasCalled).isTrue();
	}
	
	@Test
	public void after_views_mother_calls_first() {
		OnViewChangedNotifier notifier = new OnViewChangedNotifier();
		OnViewChangedNotifier.replaceNotifier(notifier);
		Child_ child = Child_.getInstance_(mock(Activity.class));
		notifier.notifyViewChanged(mock(HasViews.class));
		assertThat(child.motherInitViewsWasCalled).isTrue();
	}

}
