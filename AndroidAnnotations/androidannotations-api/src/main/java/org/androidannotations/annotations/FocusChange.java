/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
 * This annotation is intended to be used on methods to receive events defined
 * by
 * {@link android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)}
 * after focus is changed on the targeted View or subclass of View.
 * <p/>
 * The annotation value should be one or several R.id.* fields that refers to
 * View or subclasses of View. If not set, the method name will be used as the
 * R.id.* field name.
 * <p/>
 * The method MAY have multiple parameter:
 * <ul>
 * <li>A {@link android.view.View} parameter to know which view has targeted
 * this event</li>
 * <li>An {@link boolean} to know the view has focus.</li>
 * </ul>
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;FocusChange(<b>R.id.myButton</b>)
 * void focusChangedOnMyButton(boolean isChecked, View button) {
 * 	// Something Here
 * }
 * 
 * &#064;FocusChange
 * void <b>myButton</b>FocusChanged(View button) {
 * 	// Something Here
 * }
 * 
 * &#064;FocusChange(<b>{R.id.myButton, R.id.myButton1}</b>)
 * void focusChangedOnSomeButtons(View button, boolean isChecked) {
 * 	// Something Here
 * }
 * 
 * &#064;FocusChange(<b>R.id.myButton</b>)
 * void focusChangedOnMyButton() {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Rostislav Chekan
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface FocusChange {
	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";
}
