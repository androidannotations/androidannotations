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
 * Should be used on {@link android.app.Activity} classes to enable usage of
 * AndroidAnnotations.
 * </p>
 * <p>
 * Your code related to injected beans should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <p>
 * Any view related code should happen in an {@link AfterViews} annotated
 * method.
 * </p>
 * <p>
 * If the class is abstract, the enhanced activity will not be generated.
 * Otherwise, it will be generated as a final class. You can use
 * AndroidAnnotations to create Abstract classes that handle common code.
 * </p>
 * <p>
 * The annotation value should be one of R.layout.* fields. If not set, no
 * content view will be set, and you should call the
 * <code>setContentView()</code> method yourself, in <code>onCreate()</code>
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * 	public void launchActivity() {
 * 		// Note the use of generated class instead of original one
 * 		MyActivityTwo_.intent(this).startActivity();
 * 	}
 * }
 * 
 * &#064;EActivity(R.layout.main)
 * public class MyActivityTwo extends Activity {
 * 
 * 	&#064;Bean
 * 	MyBean myBean;
 * 
 * 	&#064;ViewById
 * 	TextView myTextView;
 * 
 * 	&#064;AfterInject
 * 	void init() {
 * 		myBean.doSomeStuff();
 * 	}
 * 
 * 	&#064;AfterViews
 * 	void initViews() {
 * 		myTextView.setText(&quot;test&quot;);
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterInject
 * @see AfterViews
 * @see Extra
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EActivity {

	/**
	 * The R.layout.* field which refer to the layout.
	 * 
	 * @return the id of the layout
	 */
	int value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource name as a string which refer to the layout.
	 * 
	 * @return the resource name of the layout
	 */
	String resName() default "";
}
