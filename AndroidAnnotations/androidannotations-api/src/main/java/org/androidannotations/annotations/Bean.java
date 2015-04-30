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
 * Injects an {@link EBean} annotated class in an enhanced class.
 * </p>
 * <p>
 * You can specify a specific implementation to inject using the value
 * attribute.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity
 * public class MyActivity extends Activity {
 * 
 * 	&#064;Bean
 * 	MyBean myBean;
 * 
 * 	&#064;Bean(MyBean2.class)
 * 	MyBean myBean2;
 * 
 * }
 * 
 * &#064;EBean
 * public class MyBean {
 * }
 * 
 * &#064;EBean
 * public class MyBean2 extends MyBean {
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see EBean
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Bean {

	/**
	 * The implementation class of the injected bean.
	 * 
	 * @return the implementation class
	 */
	Class<?> value() default Void.class;
}
