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
package org.androidannotations.test15.ebean;

import static org.fest.assertions.api.Assertions.assertThat;

import org.androidannotations.test15.EmptyActivityWithoutLayout_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SomeBeanTest {

	@Test
	public void getInstanceReturnsSameInstance() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeBean_ firstInstance = SomeBean_.getInstance_(context);
		SomeBean_ secondInstance = SomeBean_.getInstance_(context);
		assertThat(firstInstance).isNotSameAs(secondInstance);
	}

	@Test
	public void injectsFactoryContext() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeBean_ bean = SomeBean_.getInstance_(context);
		assertThat(bean.context).isSameAs(context);
	}

	@Test
	public void rebindChangesContext() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeBean_ bean = SomeBean_.getInstance_(context);

		EmptyActivityWithoutLayout_ context2 = new EmptyActivityWithoutLayout_();
		bean.rebind(context2);
		assertThat(bean.context).isSameAs(context2);
	}

}
