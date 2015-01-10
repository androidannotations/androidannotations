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
 * Methods annotated with @{@link AfterExtras} will be called after the Extras
 * from an Intent have been injected.
 * 
 * Any code depending on injected extras should be done in an
 * {@link AfterExtras} annotated method.
 * 
 * The method must have zero parameters.
 * 
 * There may be several methods annotated with @{@link AfterExtras} in the same
 * class.
 * 
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * 	&#064;Extra
 * 	String myExtra;
 * 
 * 	&#064;AfterExtras
 * 	void afterExtras() {
 * 		// myExtra is now available
 * 	}
 * }
 * </pre>
 *
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AfterExtras {
}
