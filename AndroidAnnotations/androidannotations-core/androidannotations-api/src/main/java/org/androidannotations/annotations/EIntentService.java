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
 * Should be used on {@link android.app.IntentService} classes to enable usage
 * of AndroidAnnotations.
 * </p>
 * <p>
 * Your code related to injected beans should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <p>
 * If the class is abstract, the enhanced intentservice will not be generated.
 * Otherwise, it will be generated as a final class. You can use
 * AndroidAnnotations to create Abstract classes that handle common code.
 * </p>
 * <p>
 * The generated class will also contain an IntentBuilder to start activity with
 * a fluent API. Android's extra can also be enhanced by using {@link Extra}
 * annotation on every native or serializable/parcelable field.
 * </p>
 * <p>
 * The IntentService class should contain a {@link ServiceAction} annotated
 * method in order to respond to an action.
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
 * 		MyIntentService_.intent(this).myAction(&quot;test&quot;, 10L).start();
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
 * 	void myAction(String valueString, long valueLong) {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterInject
 * @see ServiceAction
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EIntentService {
}
