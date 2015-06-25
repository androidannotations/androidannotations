/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.ormlite;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;
import static org.fest.reflect.core.Reflection.staticField;

import org.fest.reflect.field.Invoker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

@RunWith(RobolectricTestRunner.class)
public class OrmLiteOnDestroyTest {

	private ActivityController<OrmLiteOnDestroyActivity_> controller;
	private Invoker<Integer> instanceCountInvoker;

	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		staticField("helper").ofType(OrmLiteSqliteOpenHelper.class).in(OpenHelperManager.class).set(null);
		staticField("helperClass").ofType(Class.class).in(OpenHelperManager.class).set(null);
		staticField("wasClosed").ofType(boolean.class).in(OpenHelperManager.class).set(false);

		instanceCountInvoker = staticField("instanceCount").ofType(int.class).in(OpenHelperManager.class);
		instanceCountInvoker.set(0);

		controller = Robolectric.buildActivity(OrmLiteOnDestroyActivity_.class).create();
	}

	@Test
	public void helperIsDestroyed() throws Exception {
		controller.destroy();

		Object databaseHelper = field("databaseHelper_").ofType(OrmLiteSqliteOpenHelper.class).in(controller.get()).get();
		assertThat(databaseHelper).isNull();
		assertThat(instanceCountInvoker.get()).isEqualTo(0);
	}
}
