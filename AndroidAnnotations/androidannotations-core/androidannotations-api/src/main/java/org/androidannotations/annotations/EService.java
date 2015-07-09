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
 * Should be used on {@link android.app.Service} classes to enable usage of
 * AndroidAnnotations.
 * </p>
 * <p>
 * Your code related to injected beans should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <p>
 * If the class is abstract, the enhanced service will not be generated.
 * Otherwise, it will be generated as a final class. You can use
 * AndroidAnnotations to create Abstract classes that handle common code.
 * </p>
 * <p>
 * The generated class will also contain an IntentBuilder to start service with
 * a fluent API.
 * </p>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * 	public void startService() {
 * 		// Note the use of generated class instead of original one
 * 		MyService_.intent(this).start();
 * 	}
 * 
 * 	public void stopService() {
 * 		// Note the use of generated class instead of original one
 * 		MyService_.intent(this).stop();
 * 	}
 * 
 * }
 * 
 * &#064;EService
 * public class MyService extends Service {
 * 
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
public @interface EService {
}
