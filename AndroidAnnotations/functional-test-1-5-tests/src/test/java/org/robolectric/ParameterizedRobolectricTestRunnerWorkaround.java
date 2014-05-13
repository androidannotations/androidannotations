package org.robolectric;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.robolectric.annotation.Config;
import org.robolectric.util.AnnotationUtil;

public class ParameterizedRobolectricTestRunnerWorkaround extends Suite {

	private Object delegate; // ParameterizedRobolectricTestRunner
	private Class<?> runnerClass; // ParameterizedRobolectricTestRunner.class
	private Field lastTestRunnerClassField; // RobolectricTestRunner.lastTestRunnerClass
	private Class<?> testRunnerClass; // ParameterizedRobolectricTestRunner$TestClassRunnerForParameters.class
	private RobolectricTestRunner testrunner;
	
	public ParameterizedRobolectricTestRunnerWorkaround(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
		
		testrunner = new RobolectricTestRunner(FakeTestClass.class);
		
		Config config = AnnotationUtil.defaultsFor(Config.class);

		Config globalConfig = Config.Implementation.fromProperties(testrunner.getConfigProperties());
		if (globalConfig != null) {
			config = new Config.Implementation(config, globalConfig);
		}
		
		AndroidManifest manifest = testrunner.getAppManifest(config);
		Method getEnvironmentMethod = RobolectricTestRunner.class.getDeclaredMethod("getEnvironment", AndroidManifest.class, Config.class);
		getEnvironmentMethod.setAccessible(true);
		SdkEnvironment environment = (SdkEnvironment) getEnvironmentMethod.invoke(testrunner, manifest, config);
		
		runnerClass = environment.getRobolectricClassLoader().loadClass(ParameterizedRobolectricTestRunner.class.getName());
		testRunnerClass = environment.getRobolectricClassLoader().loadClass(runnerClass.getName() + "$TestClassRunnerForParameters");
		
		lastTestRunnerClassField = RobolectricTestRunner.class.getDeclaredField("lastTestRunnerClass");
		lastTestRunnerClassField.setAccessible(true);
		
		lastTestRunnerClassField.set(testrunner, testRunnerClass);
		
		Constructor<?> constructor = runnerClass.getConstructor(Class.class);
		delegate = constructor.newInstance(environment.getRobolectricClassLoader().loadClass(klass.getName()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Runner> getChildren() {
		try {
			lastTestRunnerClassField.set(testrunner, testRunnerClass);
			
			Method getChildrenMethod = runnerClass.getDeclaredMethod("getChildren");
			getChildrenMethod.setAccessible(true);
			return (List<Runner>) getChildrenMethod.invoke(delegate);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static class FakeTestClass { 
		
		@Test
		public void test() {}
	}
	
}
