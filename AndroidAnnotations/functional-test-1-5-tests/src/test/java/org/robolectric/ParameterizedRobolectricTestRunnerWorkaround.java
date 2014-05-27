package org.robolectric;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.fest.reflect.reference.TypeRef;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.robolectric.annotation.Config;

import static org.fest.reflect.core.Reflection.*;

public class ParameterizedRobolectricTestRunnerWorkaround extends Suite {

	private Object delegate; // ParameterizedRobolectricTestRunner
	private Class<?> runnerClass; // ParameterizedRobolectricTestRunner.class
	private Class<?> testRunnerClass; // ParameterizedRobolectricTestRunner$TestClassRunnerForParameters.class
	private RobolectricTestRunner testrunner;
	private List<Runner> runners; // delegate.getChildren()
	
	public ParameterizedRobolectricTestRunnerWorkaround(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
		
		testrunner = new ClassLoaderCreatorRobolectricTestRunner(klass);
		
		Config config = testrunner.getConfig(klass.getMethods()[0]);
		
		AndroidManifest manifest = testrunner.getAppManifest(config);
		SdkEnvironment environment  = method("getEnvironment")
				.withReturnType(SdkEnvironment.class).withParameterTypes(AndroidManifest.class, Config.class).in(testrunner).invoke(manifest, config);
		
		runnerClass = type(ParameterizedRobolectricTestRunner.class.getName()).withClassLoader(environment.getRobolectricClassLoader()).load();
		testRunnerClass = type(runnerClass.getName() + "$TestClassRunnerForParameters").withClassLoader(environment.getRobolectricClassLoader()).load();
		
		Field lastTestRunnerClassField = field("lastTestRunnerClass").ofType(Class.class).in(RobolectricTestRunner.class).info();
		lastTestRunnerClassField.setAccessible(true);
		lastTestRunnerClassField.set(testrunner, testRunnerClass);
		
		delegate = constructor().withParameterTypes(Class.class).in(runnerClass)
				.newInstance(type(klass.getName()).withClassLoader(environment.getRobolectricClassLoader()).load());
		
		Field lastSdkEnvironmentField = field("lastSdkEnvironment").ofType(SdkEnvironment.class).in(RobolectricTestRunner.class).info();
		lastSdkEnvironmentField.setAccessible(true);
		
		Field lastSdkConfigField = field("lastSdkConfig").ofType(SdkConfig.class).in(RobolectricTestRunner.class).info();
		lastSdkConfigField.setAccessible(true);
		
		runners = method("getChildren").withReturnType(new TypeRef<List<Runner>>() {}) .in(delegate).invoke();
		
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

		public ClassLoaderCreatorRobolectricTestRunner(Class<?> testClass)
				throws InitializationError, IllegalArgumentException, IllegalAccessException {
			super(testClass);
			
			Map<Class<? extends RobolectricTestRunner>, EnvHolder> envHoldersByTestRunner = 
					field("envHoldersByTestRunner").ofType(new TypeRef<Map<Class<? extends RobolectricTestRunner>, EnvHolder>>() {}).in(this).get();
			
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
