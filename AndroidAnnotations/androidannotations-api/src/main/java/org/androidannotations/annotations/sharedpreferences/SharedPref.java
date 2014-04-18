/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.annotations.sharedpreferences;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.content.Context;

/**
 * Apply @{@link SharedPref} on an interface to create a SharedPreference helper
 * that will contain access methods related to the methods you define in the
 * interface.
 * <p/>
 * You should then inject your SharedPreference generated class by using
 * {@link Pref} annotation.
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;Pref
 * 	<b>mypackage.MyPref_</b> myPref;
 * }
 * 
 * 
 * package <b>mypackage</b>;
 * 
 * &#064;SharedPref
 * public interface <b>MyPref</b> {
 * 
 * 	// The field name will have default value "John"
 * 	&#064;DefaultString("John")
 * 	String name();
 * 
 * 	// The field age will have default value 42
 * 	&#064;DefaultInt(42)
 * 	int age();
 * 
 * 	// The field lastUpdated will have default value 0
 * 	long lastUpdated();
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Pref
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface SharedPref {
	public enum Scope {
		APPLICATION_DEFAULT, ACTIVITY, ACTIVITY_DEFAULT, UNIQUE;
	}

	Scope value() default Scope.ACTIVITY;

	int mode() default Context.MODE_PRIVATE;
}
