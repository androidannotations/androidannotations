/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.app.Activity;
import android.os.Parcelable;

/**
 * Use on any native, {@link Parcelable} or {@link Serializable} field in an
 * {@link EActivity} annotated class to bind it with Android's extra.
 * <p/>
 * The annotation value is the key used for extra. If not set, the field name
 * will be used as the key.
 * <p/>
 * When {@link Extra} is used, the intent builder will hold dedicated methods
 * for each annotated fields.
 * <p/>
 * Your code related to injected extra should go in an {@link AfterInject}
 * annotated method.
 * <p/>
 * Calling {@link Activity#setIntent(android.content.Intent)} will automatically
 * update the annotated extras.
 * <p/>
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

	String value() default "";
}
