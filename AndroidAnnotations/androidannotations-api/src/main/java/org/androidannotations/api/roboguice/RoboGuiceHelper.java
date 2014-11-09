package org.androidannotations.api.roboguice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection utils to call methods which are accessible in RoboGuice package
 * but not from outside.
 */
public class RoboGuiceHelper {

	public static void callInjectViews(Object activity) {
		try {
			Class<?> viewMembersInjectorClass = Class.forName("roboguice.inject.ViewListener$ViewMembersInjector");
			Method injectViewsMethod = viewMembersInjectorClass.getDeclaredMethod("injectViews", Object.class);
			injectViewsMethod.setAccessible(true);
			injectViewsMethod.invoke(null, activity);
		} catch (ClassNotFoundException e) {
			propagateRuntimeException(e);
		} catch (NoSuchMethodException e) {
			propagateRuntimeException(e);
		} catch (SecurityException e) {
			propagateRuntimeException(e);
		} catch (IllegalAccessException e) {
			propagateRuntimeException(e);
		} catch (IllegalArgumentException e) {
			propagateRuntimeException(e);
		} catch (InvocationTargetException e) {
			propagateRuntimeException(e);
		}
	}

	private static void propagateRuntimeException(Throwable t) {
		throw new RuntimeException("Could not invoke RoboGuice method!", t);
	}
}
