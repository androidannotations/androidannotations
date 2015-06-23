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
package org.robolectric;

import static org.fest.reflect.core.Reflection.constructor;
import static org.fest.reflect.core.Reflection.field;
import static org.fest.reflect.core.Reflection.method;
import static org.fest.reflect.core.Reflection.type;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.fest.reflect.reference.TypeRef;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.robolectric.annotation.Config;

public class ParameterizedRobolectricTestRunnerWorkaround extends Suite {

	private Object delegate; // ParameterizedRobolectricTestRunner
	private Class<?> runnerClass; // ParameterizedRobolectricTestRunner.class
	private Class<?> testRunnerClass; // ParameterizedRobolectricTestRunner$TestClassRunnerForParameters.class
	private RobolectricTestRunner testrunner;
	private List<Runner> runners; // delegate.getChildren()

	public ParameterizedRobolectricTestRunnerWorkaround(Class<?> klass) throws Exception {
		super(klass, Collections.<Runner> emptyList());

		testrunner = new ClassLoaderCreatorRobolectricTestRunner(klass);

		Config config = testrunner.getConfig(klass.getMethods()[0]);

		AndroidManifest manifest = testrunner.getAppManifest(config);
		SdkEnvironment environment = method("getEnvironment").withReturnType(SdkEnvironment.class).withParameterTypes(AndroidManifest.class, Config.class).in(testrunner).invoke(manifest, config);

		runnerClass = type(ParameterizedRobolectricTestRunner.class.getName()).withClassLoader(environment.getRobolectricClassLoader()).load();
		testRunnerClass = type(runnerClass.getName() + "$TestClassRunnerForParameters").withClassLoader(environment.getRobolectricClassLoader()).load();

		Field lastTestRunnerClassField = field("lastTestRunnerClass").ofType(Class.class).in(RobolectricTestRunner.class).info();
		lastTestRunnerClassField.setAccessible(true);
		lastTestRunnerClassField.set(testrunner, testRunnerClass);

		delegate = constructor().withParameterTypes(Class.class).in(runnerClass).newInstance(type(klass.getName()).withClassLoader(environment.getRobolectricClassLoader()).load());

		Field lastSdkEnvironmentField = field("lastSdkEnvironment").ofType(SdkEnvironment.class).in(RobolectricTestRunner.class).info();
		lastSdkEnvironmentField.setAccessible(true);

		Field lastSdkConfigField = field("lastSdkConfig").ofType(SdkConfig.class).in(RobolectricTestRunner.class).info();
		lastSdkConfigField.setAccessible(true);

		runners = method("getChildren").withReturnType(new TypeRef<List<Runner>>() {
		}).in(delegate).invoke();

		for (Runner runner : runners) {
			lastTestRunnerClassField.set(runner, testRunnerClass);
			lastSdkEnvironmentField.set(runner, environment);
			lastSdkConfigField.set(runner, environment.getSdkConfig());
		}
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	private static class ClassLoaderCreatorRobolectricTestRunner extends RobolectricTestRunner {

		public ClassLoaderCreatorRobolectricTestRunner(Class<?> testClass) throws Exception {
			super(testClass);

			Map<Class<? extends RobolectricTestRunner>, EnvHolder> envHoldersByTestRunner = field("envHoldersByTestRunner") //
					.ofType(new TypeRef<Map<Class<? extends RobolectricTestRunner>, EnvHolder>>() {
					})//
					.in(this)//
					.get();

			EnvHolder envHolder = envHoldersByTestRunner.get(RobolectricTestRunner.class);

			if (envHolder != null) {
				Field envHolderField = field("envHolder").ofType(EnvHolder.class).in(RobolectricTestRunner.class).info();
				envHolderField.setAccessible(true);
				envHolderField.set(this, envHolder);
			} else {
				envHoldersByTestRunner.put(RobolectricTestRunner.class, envHolder);
			}
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}
	}
}
