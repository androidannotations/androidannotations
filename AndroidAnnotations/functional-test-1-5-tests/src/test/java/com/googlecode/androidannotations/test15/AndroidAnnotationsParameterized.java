package com.googlecode.androidannotations.test15;

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

/**
 * This class is a copy of Junit {@link Parameterized}, but it creates
 * {@link AndroidAnnotationsTestRunner} runners instead of
 * {@link BlockJUnit4ClassRunner}
 * 
 * We added some hacks, because Robolectric has a lot of expectations on how the
 * runner and the tested class should work.
 * 
 * <p>
 * The custom runner <code>Parameterized</code> implements parameterized tests.
 * When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * </p>
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
 * 	public FibonacciTest(int input, int expected) {
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
public class AndroidAnnotationsParameterized extends Suite {

	public static class TestClassRunnerForParameters extends AndroidAnnotationsTestRunner {
		private int fParameterSetNumber;

		private List<Object[]> fParameterList;

		private AndroidAnnotationsParameterized motherRunner;

		public TestClassRunnerForParameters(Class<?> type) throws InitializationError {
			super(type);
		}

		private void init(AndroidAnnotationsParameterized motherRunner, List<Object[]> parameterList, int i) {
			this.motherRunner = motherRunner;
			fParameterList = parameterList;
			fParameterSetNumber = i;
		}

		@Override
		public Object createTest() throws Exception {
			Object test = super.createTest();

			if (motherRunner == null) {
				/*
				 * We are in the delegate runner created by Robolectric
				 */
				return test;
			}

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
				return fParameterList.get(fParameterSetNumber);
			} catch (ClassCastException e) {
				throw new Exception(String.format("%s.%s() must return a Collection of arrays.", getTestClass().getName(), motherRunner.getParametersMethod(getTestClass()).getName()));
			}
		}

		@Override
		protected String getName() {
			return String.format("[%s]", fParameterSetNumber);
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(), fParameterSetNumber);
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
	public AndroidAnnotationsParameterized(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner> emptyList());
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

}
