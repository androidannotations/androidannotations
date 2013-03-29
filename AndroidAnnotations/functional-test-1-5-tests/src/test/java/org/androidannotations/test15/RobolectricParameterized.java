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
package org.androidannotations.test15;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.bytecode.RobolectricClassLoader;

/**
 * This class is a copy of Junit {@link Parameterized}, but it creates
 * {@link AndroidAnnotationsTestRunner} runners instead of
 * {@link BlockJUnit4ClassRunner}
 * 
 * We added some hacks, because Robolectric has a lot of expectations on how the
 * runner and the tested class should work.
 * 
 * 
 * <p>
 * The custom runner <code>Parameterized</code> implements parameterized tests.
 * When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * </p>
 * 
 * <p>
 * The test must have a constructor with no parameters. The parameters are
 * passed through an init() method, which can have any number of parameters. The
 * parameters of the init() method must match the {@link Parameterized} test
 * data.
 * <p/>
 * 
 * For example, to test a Fibonacci function, write:
 * 
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class FibonacciTest {
 * 	&#064;Parameters
 * 	public static List&lt;Object[]&gt; data() {
 * 		return Arrays.asList(new Object[][] { Fibonacci, { { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } } });
 * 	}
 * 
 * 	private int fInput;
 * 
 * 	private int fExpected;
 * 
 * 	public void init(int input, int expected) {
 * 		fInput = input;
 * 		fExpected = expected;
 * 	}
 * 
 * 	&#064;Test
 * 	public void test() {
 * 		assertEquals(fExpected, Fibonacci.compute(fInput));
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * Each instance of <code>FibonacciTest</code> will be constructed using the
 * two-argument constructor and the data values in the
 * <code>&#064;Parameters</code> method.
 * </p>
 */
public class RobolectricParameterized extends Suite {

	public static class TestClassRunnerForParameters extends AndroidAnnotationsTestRunner {
		private int parameterSetNumber;

		private List<Object[]> parameterList;

		private RobolectricParameterized motherRunner;

		public TestClassRunnerForParameters(Class<?> type) throws InitializationError {
			super(type);
		}

		private void init(RobolectricParameterized motherRunner, List<Object[]> parameterList, int parameterSetNumber) {
			this.motherRunner = motherRunner;
			this.parameterList = parameterList;
			this.parameterSetNumber = parameterSetNumber;
		}

		@Override
		public Object createTest() throws Exception {
			Object test = super.createTest();

			if (motherRunner == null) {
				/*
				 * We are in the delegate runner created by Robolectric.
				 */
				return test;
			}

			/*
			 * We are in the original runner, but the test instance is the one
			 * created by the delegate runner.
			 */

			/*
			 * Let's init the test with parameters. We look for a method called
			 * init, with any params. We can't use constructor parameters,
			 * Robolectric won't allow that.
			 */

			Method[] declaredMethods = test.getClass().getDeclaredMethods();

			/*
			 * We only take first init method found in consideration
			 */
			Method initMethod = null;
			for (Method method : declaredMethods) {
				if (method.getName().equals("init")) {
					initMethod = method;
					break;
				}
			}

			if (initMethod == null) {
				throw new RuntimeException("No init method found in parameterized test");
			}

			initMethod.setAccessible(true);

			Object[] params = computeParams();

			initMethod.invoke(test, params);

			return test;
		}

		private Object[] computeParams() throws Exception {
			try {
				return parameterList.get(parameterSetNumber);
			} catch (ClassCastException e) {
				throw new Exception(String.format("%s.%s() must return a Collection of arrays.", getTestClass().getName(), motherRunner.getParametersMethod(getTestClass()).getName()));
			}
		}

		@Override
		protected String getName() {
			return String.format("[%s]", parameterSetNumber);
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(), parameterSetNumber);
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
	}

	private final ArrayList<Runner> runners = new ArrayList<Runner>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public RobolectricParameterized(Class<?> klass) throws Throwable {
		/*
		 * Notice how we replace the class with a class loaded by robolectric.
		 */
		super(robolectricClass(klass), Collections.<Runner> emptyList());
		List<Object[]> parametersList = getParametersList(getTestClass());
		for (int i = 0; i < parametersList.size(); i++) {
			TestClassRunnerForParameters testRunner = new TestClassRunnerForParameters(getTestClass().getJavaClass());
			testRunner.init(this, parametersList, i);
			runners.add(testRunner);
		}
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList(TestClass klass) throws Throwable {
		return (List<Object[]>) getParametersMethod(klass).invokeExplosively(null);
	}

	private FrameworkMethod getParametersMethod(TestClass testClass) throws Exception {
		List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);
		for (FrameworkMethod each : methods) {
			int modifiers = each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class " + testClass.getName());
	}

	private static Class<?> robolectricClass(Class<?> originalClass) throws Exception {
		return getRobolectricLoader().loadClass(originalClass.getName());
	}

	private static RobolectricClassLoader getRobolectricLoader() throws Exception {
		Method getDefaultLoader = RobolectricTestRunner.class.getDeclaredMethod("getDefaultLoader");
		getDefaultLoader.setAccessible(true);
		return (RobolectricClassLoader) getDefaultLoader.invoke(null);
	}

}
