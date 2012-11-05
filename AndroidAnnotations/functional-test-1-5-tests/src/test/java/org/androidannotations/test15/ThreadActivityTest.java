/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.androidannotations.api.BackgroundExecutor;

@RunWith(AndroidAnnotationsTestRunner.class)
public class ThreadActivityTest {

	private ThreadActivity_ activity;

	@Before
	public void setup() {
		activity = new ThreadActivity_();
		activity.onCreate(null);
	}

	@Test
	public void backgroundDelegatesToExecutor() {
		
		Executor executor = mock(Executor.class);
		
		BackgroundExecutor.setExecutor(executor);
		
		activity.emptyBackgroundMethod();
		
		verify(executor).execute(Mockito.<Runnable>any());
	}

}
