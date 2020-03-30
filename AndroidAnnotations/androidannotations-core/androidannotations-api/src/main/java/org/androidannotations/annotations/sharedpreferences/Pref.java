/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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

/**
 * <p>
 * Injects an {@link SharedPref} annotated class in any enhanced class.
 * </p>
 * <p>
 * The field or method parameter MUST be of a type that is generated using
 * {@link SharedPref} and therefore extends
 * {@link org.androidannotations.api.sharedpreferences.SharedPreferencesHelper
 * SharedPreferencesHelper}.
 * </p>
 * <p>
 * <b>Note:</b> To prevent you from any building issues, you should use fully
 * qualified name for the field type.
 * </p>
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
 * 
 * 	&#064;Pref
 * 	void methodInjection(<b>mypackage.MyPref_</b> myPref) {
 * 		// do stuff	
 * 	}	
 * }
 * 
 * 
 * package <b>mypackage</b>;
 * 
 * &#064;SharedPref
 * public interface <b>MyPref</b> {
 * 
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see SharedPref
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Pref {
}
