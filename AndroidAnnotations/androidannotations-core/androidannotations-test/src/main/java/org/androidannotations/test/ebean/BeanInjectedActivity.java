/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.test.ebean;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;

@EActivity
public class BeanInjectedActivity extends Activity {
	
	@Bean
	public EmptyDependency dependency;
	
	@Bean(SomeImplementation.class)
	public SomeInterface interfaceDependency;
	
	@Bean
	public SomeSingleton singletonDependency;

	public EmptyDependency methodInjectedDependency;
	public SomeSingleton methodInjectedSingleton;
	public SomeInterface methodInjectedInterface;

	public EmptyDependency annotatedParamDependency;
	public SomeSingleton annotatedParamSingleton;
	public SomeInterface annotatedParamInterface;

	public EmptyDependency multiDependency;
	public SomeSingleton multiDependencySingleton;
	public SomeInterface multiDependencyInterface;

	@Bean
	protected void injectDependency(EmptyDependency methodInjectedDependency) {
		this.methodInjectedDependency = methodInjectedDependency;
	}

	@Bean(SomeImplementation.class)
	protected void injectInterface(SomeInterface methodInjectedInterface) {
		this.methodInjectedInterface = methodInjectedInterface;
	}

	@Bean
	protected void injectSingleton(SomeSingleton methodInjectedSingleton) {
		this.methodInjectedSingleton = methodInjectedSingleton;
	}

	protected void injectDependencyAnnotatedParam(
			@Bean EmptyDependency annotatedParamDependency) {
		this.annotatedParamDependency = annotatedParamDependency;
	}

	protected void injectInterfaceAnnotatedParam(
			@Bean(SomeImplementation.class) SomeInterface annotatedParamInterface) {
		this.annotatedParamInterface = annotatedParamInterface;
	}

	protected void injectSingletonAnnotatedParam(
			@Bean SomeSingleton annotatedParamSingleton) {
		this.annotatedParamSingleton = annotatedParamSingleton;
	}

	protected void injectMultipleDependencies(
			@Bean EmptyDependency multiDependency,
			@Bean(SomeImplementation.class) SomeInterface multiDependencyInterface,
			@Bean SomeSingleton multiDependencySingleton) {
		this.multiDependency = multiDependency;
		this.multiDependencyInterface = multiDependencyInterface;
		this.multiDependencySingleton = multiDependencySingleton;
	}
}
