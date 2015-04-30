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
 * Should be used on Activity classes that use the RoboGuice framework.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * &#064;RoboGuice({ AstroListener.class, AnotherListener.class })
 * public class AstroGirl extends Activity {
 * 
 * 	&#064;ViewById
 * 	EditText edit;
 * 
 * 	&#064;Inject
 * 	GreetingService greetingService;
 * 
 * 	&#064;Click
 * 	void button() {
 * 		String name = edit.getText().toString();
 * 		greetingService.greet(name);
 * 	}
 * }
 * 
 * public class AstroListener {
 * 
 * 	&#064;Inject
 * 	Context context;
 * 
 * 	public void doSomethingOnResume(@Observes OnResumeEvent onResume) {
 * 		Toast.makeText(context, &quot;Activity has been resumed&quot;, Toast.LENGTH_LONG).show();
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see <a
 *      href="https://github.com/excilys/androidannotations/wiki/RoboGuiceIntegration">RoboGuiceIntegration</a>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface RoboGuice {
	/**
	 * The RoboGuice listener classes to bind to this activity. Will add an
	 * injected listener field to the subclass.
	 * 
	 * @return the RoboGuice listener classes
	 */
	Class<?>[] value() default {};
}
