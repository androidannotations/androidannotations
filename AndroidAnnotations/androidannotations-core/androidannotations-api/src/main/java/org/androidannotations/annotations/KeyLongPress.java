/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
 * This annotation is intended to be used on methods to receive long press event
 * on a key. This annotation can be used on methods of classes which implements
 * {@link android.view.KeyEvent.Callback}.
 * </p>
 * <p>
 * The annotation value should be one or several of
 * {@link android.view.KeyEvent} constant fields which name contains KEYCODE. If
 * not set, the method name will be used as the {@link android.view.KeyEvent}
 * .KEYCODE_* field name.
 * </p>
 * <p>
 * The method may return a <code>boolean</code>, <code>void</code>, or a
 * {@link Boolean}. If returning void, it will be considered as returning true
 * (ie: the method has handled the event).
 * </p>
 * <p>
 * The method MAY have one parameter:
 * </p>
 * <ul>
 * <li>A {@link android.view.KeyEvent} parameter to know which key has been down
 * </li>
 * </ul>
 *
 * <p>
 * Example :
 * </p>
 *
 * <pre>
 * &#064;EActivity
 * public class MyActivity extends Activity {
 *
 * 	&#064;KeyLongPress
 * 	void enter() {
 * 		// ...
 * 	}
 *
 * 	&#064;KeyLongPress
 * 	void onEnter() {
 * 		// ...
 * 	}
 *
 * 	&#064;KeyLongPress
 * 	void onEnterPressed() {
 * 		// ...
 * 	}
 *
 * 	&#064;KeyLongPress
 * 	void enterPressed() {
 * 		// ...
 * 	}
 *
 * 	&#064;KeyLongPress(KeyEvent.KEYCODE_0)
 * 	void keyZeroIsLongPressed(KeyEvent keyEvent) {
 * 		// ...
 * 	}
 *
 * 	&#064;KeyLongPress({ KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_N })
 * 	boolean multipleKeys(KeyEvent keyEvent) {
 * 		return false;
 * 	}
 * }
 * </pre>
 *
 * @see android.view.KeyEvent
 * @see android.view.KeyEvent.Callback
 * @see android.view.KeyEvent.Callback#onKeyLongPress(int,
 *      android.view.KeyEvent)
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface KeyLongPress {

	/**
	 * The {@link android.view.KeyEvent} class constants which name contains
	 * KEYCODE.
	 *
	 * @return the value of the key code
	 */
	int[] value() default {};
}
