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
package org.androidannotations.test15.eview;

import static org.fest.assertions.api.Assertions.assertThat;

import org.androidannotations.test15.EmptyActivityWithoutLayout_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;

@RunWith(RobolectricTestRunner.class)
public class CustomButtonTest {

	@Test
	public void constructorParametersAreTransmittedFromFactoryMethod() {
		Context context = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		int parameter = 42;
		CustomButton button = CustomButton_.build(context, parameter);
		assertThat(button.constructorParameter).isEqualTo(parameter);
	}

	@Test
	public void factoryMethodBuildsInflatedInstance() {
		Context context = Robolectric.buildActivity(EmptyActivityWithoutLayout_.class).create().get();
		CustomButton button = CustomButton_.build(context);
		assertThat(button.afterViewsCalled).isTrue();
	}

}
