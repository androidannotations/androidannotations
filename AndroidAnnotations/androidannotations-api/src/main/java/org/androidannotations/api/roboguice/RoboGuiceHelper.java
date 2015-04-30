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
package org.androidannotations.api.roboguice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection utils to call methods which are accessible in RoboGuice package
 * but not from outside.
 */
public class RoboGuiceHelper {

	private RoboGuiceHelper() {
	}

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
