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
package org.androidannotations.test15.eview;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.androidannotations.test15.EmptyActivityWithoutLayout_;

@RunWith(AndroidAnnotationsTestRunner.class)
public class CustomButtonTest {

	@Test
	public void constructor_parameters_are_transmitted_from_factory_method() {
		Context context = new EmptyActivityWithoutLayout_();
		int parameter = 42;
		CustomButton button = CustomButton_.build(context, parameter);
		assertThat(button.constructorParameter).isEqualTo(parameter);
	}

	@Test
	public void factory_method_builds_inflated_instance() {
		Context context = new EmptyActivityWithoutLayout_();
		CustomButton button = CustomButton_.build(context);
		assertThat(button.afterViewsCalled).isTrue();
	}

}
