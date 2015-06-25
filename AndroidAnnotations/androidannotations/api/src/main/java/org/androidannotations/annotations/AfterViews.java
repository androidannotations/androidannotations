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
 * Methods annotated with @{@link AfterViews} will be called after
 * {@link android.app.Activity#setContentView(int) setContentView(int)} is
 * called by the generated activity.
 * </p>
 * <p>
 * This occurs AFTER <code>setContentView(View)</code> which is called at the
 * end of super.onCreate(). Any view depending code should be done in an
 * {@link AfterViews} annotated method.
 * </p>
 * <p>
 * The method MUST have zero parameters.
 * </p>
 * <p>
 * There MAY be several methods annotated with @{@link AfterViews} in the same
 * activity.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivityTwo extends Activity {
 * 
 * 	&#064;ViewById
 * 	TextView myTextView;
 * 
 * 	&#064;AfterViews
 * 	void initViews() {
 * 		myTextView.setText(&quot;test);
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterInject
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AfterViews {
}
