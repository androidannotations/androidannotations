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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.view.MotionEvent;

/**
 * This annotation is intended to be used on methods to receive events defined
 * by
 * {@link android.view.View.OnTouchListener#onTouch(android.view.View, MotionEvent)}
 * when the view has been touched by the user.
 * <p/>
 * The annotation value should be one of R.id.* fields. If not set, the method
 * name will be used as the R.id.* field name.
 * <p/>
 * The method may return a <code>boolean</code>, <code>void</code>, or a
 * {@link Boolean}. If returning void, it will be considered as returning true
 * (ie the method has handled the event).
 * <p/>
 * The method MAY have one or two parameters:
 * <ul>
 * <li>A {@link android.view.View} parameter to know which view has been clicked
 * <li>A {@link android.view.MotionEvent} parameter
 * </ul>
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;Touch(<b>R.id.myButton</b>)
 * void touchOnMyButton() {
 * 	// Something Here
 * }
 * 
 * &#064;Touch
 * void <b>myButton</b>Touched(View view) {
 * 	// Something Here
 * }
 * 
 * &#064;Touch
 * void <b>myButton</b>Touched(View view, MotionEvent motionEvent) {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Click
 * @see LongClick
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Touch {
	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";
}
