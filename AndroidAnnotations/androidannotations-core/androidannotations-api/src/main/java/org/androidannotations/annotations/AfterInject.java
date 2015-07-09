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
 * Methods annotated with @{@link AfterInject} will be called after the
 * constructor is called in an enhanced class. Any code depending on injected
 * fields should be done in an {@link AfterInject} annotated method.
 * </p>
 * <p>
 * The method MUST have zero parameters.
 * </p>
 * <p>
 * There MAY be several methods annotated with @{@link AfterInject} in the same
 * class.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivityTwo extends Activity {
 * 
 * 	&#064;Bean
 * 	MyBean myBean;
 * 
 * 	&#064;AfterInject
 * 	void init() {
 * 		myBean.doSomeStuff();
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterViews
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AfterInject {
}
