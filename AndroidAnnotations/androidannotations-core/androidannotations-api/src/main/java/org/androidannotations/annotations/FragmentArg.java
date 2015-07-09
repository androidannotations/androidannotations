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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Use on any native, {@link android.os.Parcelable Parcelable} or
 * {@link java.io.Serializable Serializable} field in an {@link EFragment}
 * annotated class to bind it with Android's arguments.
 * </p>
 * <p>
 * The annotation value is the key used for argument. If not set, the field name
 * will be used as the key.
 * </p>
 * <p>
 * When {@link FragmentArg} is used, the intent builder will hold dedicated
 * methods for each annotated fields.
 * </p>
 * <p>
 * Your code related to injected extra should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EFragment
 * public class MyFragment extends Fragment {
 * 
 * 	&#064;FragmentArg
 * 	String myMessage;
 * }
 * 
 * &#064;EActivity
 * public class MyActivity extends Activity {
 * 
 * 	&#064;AfterViews
 * 	void init() {
 * 		MyFragment myFragment = MyFragment_.builder() //
 * 				.myMessage(&quot;Hello&quot;) //
 * 				.build();
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see EFragment
 * @see FragmentById
 * @see FragmentByTag
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface FragmentArg {

	/**
	 * The key of the injected Fragment argument.
	 * 
	 * @return the key of the argument
	 */
	String value() default "";
}
