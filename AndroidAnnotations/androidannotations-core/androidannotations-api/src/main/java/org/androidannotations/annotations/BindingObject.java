/**
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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Can be used on fields, methods and method parameters in enhanced Activities,
 * Fragments and ViewGroups to access the binding object used in Data Binding.
 * </p>
 * <p>
 * The declaring class must be annotated with {@link DataBound}. The type must
 * extends <code>ViewDataBinding</code>. The following injections are allowed:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;DataBound
 * &#064;EActivity(R.layout.my_activity)
 * public class MyActivity extends Activity {
 *
 * 	&#064;BindingObject
 * 	MyActivityBinding binding;
 *
 * 	&#064;BindingObject
 * 	void methodInjection(MyActivityBinding binding) {
 * 		// use binding
 * 	}
 *
 * 	void paramInjection(&#064;BindingObject MyActivityBinding binding) {
 * 		// use binding
 * 	}
 *
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see DataBound
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface BindingObject {
}
