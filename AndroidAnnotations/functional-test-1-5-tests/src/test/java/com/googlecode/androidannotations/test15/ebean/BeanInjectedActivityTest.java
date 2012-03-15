/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.test15.ebean;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;

@RunWith(AndroidAnnotationsTestRunner.class)
public class BeanInjectedActivityTest {
	
	private BeanInjectedActivity_ activity;

	@Before
	public void setup() {
		activity = new BeanInjectedActivity_();
		activity.onCreate(null);
		
	}

	@Test
	public void dependency_is_injected() {
		assertThat(activity.dependency).isNotNull();
	}
	
	@Test
	public void dependency_with_annotation_value_is_injected() {
		assertThat(activity.interfaceDependency).isNotNull();
	}
	
	@Test
	public void dependency_with_annotation_value_is_of_annotation_value_type() {
		assertThat(activity.interfaceDependency).isInstanceOf(SomeImplementation.class);
	}
	
	@Test
	public void singleton_dependency_is_same_reference() {
		SingletonDependency initialDependency = activity.singletonDependency;
		
		BeanInjectedActivity_ newActivity = new BeanInjectedActivity_();
		newActivity.onCreate(null);
		
		assertThat(newActivity.singletonDependency).isSameAs(initialDependency);
	}
	
}
