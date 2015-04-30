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
 * {@link java.io.Serializable Serializable} field in an {@link EActivity}
 * annotated class to bind it with Android's extra.
 * </p>
 * <p>
 * The annotation value is the key used for extra. If not set, the field name
 * will be used as the key.
 * </p>
 * <p>
 * When {@link Extra} is used, the intent builder will hold dedicated methods
 * for each annotated fields.
 * </p>
 * <p>
 * Your code related to injected extra should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <p>
 * Calling {@link android.app.Activity#setIntent(android.content.Intent)
 * Activity#setIntent(Intent)} will automatically update the annotated extras.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity
 * public class MyActivity extends Activity {
 * 
 * 	&#064;Click
 * 	void buttonClicked() {
 * 		MyExtraActivity_.intent(this) //
 * 				.myMessage(&quot;test&quot;) //
 * 				.startActivity();
 * 	}
 * }
 * 
 * &#064;EActivity
 * public class MyExtraActivity extends Activity {
 * 
 * 	&#064;Extra
 * 	String myMessage;
 * 
 * 	&#064;AfterInject
 * 	void init() {
 * 		Log.d(&quot;AA&quot;, &quot;extra myMessage = &quot; + myMessage);
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterInject
 * @see EActivity
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Extra {

	/**
	 * The key of the injected extra.
	 * 
	 * @return the key of the extra
	 */
	String value() default "";
}
