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
 * Should be used on a method that must respond to a specific action in an
 * {@link EIntentService} annotated class. The method name will be used as
 * action name unless the {@link #value()} field is set.
 * </p>
 * <p>
 * The method signature (ie with attributes) will be a part of the IntentBuilder
 * generated for the {@link EIntentService}.
 * </p>
 * <p>
 * The method could contain any type or parameters.
 * </p>
 * <p>
 * The class MAY contain several {@link ServiceAction} annotated methods.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * 	public void launchAction() {
 * 		// Note the use of generated class instead of original one
 * 		MyIntentService_.intent(this)
 * 				.&lt;b&gt;myAction&lt;/b&gt;("test", 10L)
 * 				.start();
 * 	}
 * 
 * }
 * 
 * &#064;EIntentService
 * public class MyIntentService extends IntentService {
 * 
 * 	&#064;ServiceAction
 * 	void mySimpleAction() {
 * 		// ...
 * 	}
 * 
 * 	&#064;ServiceAction
 * 	void &lt;b&gt;myAction&lt;/b&gt;(String valueString, long valueLong) {
 * 		// ...
 * 	}
 * 
 * 	&#064;Override
 * 	protected void onHandleIntent(Intent intent) {
 * 	 	// empty, will be overridden in generated subclass 	
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * Note: Since
 * {@link android.app.IntentService#onHandleIntent(android.content.Intent)
 * IntentService#onHandleIntent} is abstract, you have to add an empty
 * implementation. For convenience, we provide the
 * {@link org.androidannotations.api.support.app.AbstractIntentService
 * AbstractIntentService} class, which implements that method, so you do not
 * have to do in your actual class if you derive it.
 * </p>
 * 
 * @see EIntentService
 * @see org.androidannotations.api.support.app.AbstractIntentService
 *      AbstractIntentService
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ServiceAction {

	/**
	 * Define the action's name. If this field isn't set the annotated method
	 * name will be used.
	 * 
	 * @return the name of the action
	 */
	String value() default "";

}
