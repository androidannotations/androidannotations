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
package org.androidannotations.test15.ormlite;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;

@RunWith(AndroidAnnotationsTestRunner.class)
public class OrmLiteActivityTest {
	
	private OrmLiteActivity_ activity;

	@Before
	public void setup() {
		activity = new OrmLiteActivity_();
		activity.onCreate(null);
	}

	@Test
	public void custom_dao_is_injected() {
		assertThat(activity.userDao).isNotNull();
	}

	@Test
	public void dao_is_injected() {
		assertThat(activity.carDao).isNotNull();
	}

	@Test
	public void bean_is_injected() {
		assertThat(activity.ormLiteBean).isNotNull();
	}

	@Test
	public void dao_in_bean_is_injected() {
		assertThat(activity.ormLiteBean.userDao).isNotNull();
	}
}
