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
package org.androidannotations.test15.nonconfiguration;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.test15.ebean.EmptyDependency;
import org.androidannotations.test15.ebean.SomeImplementation;
import org.androidannotations.test15.ebean.SomeInterface;

import android.app.Activity;

@EActivity
public class NonConfigurationActivity extends Activity {

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Robolectric does not create a new instance after Activity.recreate
		// we simulate the new instance by clearing our fields

		maintainedDependency = null;
		recreatedDependency = null;
		maintainedAbstracted = null;
		someObject = null;
	}

	@Bean
	@NonConfigurationInstance
	EmptyDependency maintainedDependency;

	@Bean
	EmptyDependency recreatedDependency;

	@Bean(SomeImplementation.class)
	@NonConfigurationInstance
	SomeInterface maintainedAbstracted;

	@NonConfigurationInstance
	Object someObject;

}
