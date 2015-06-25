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
 * This annotation is intended to be used on methods to receive events defined
 * by
 * {@link android.view.View.OnLongClickListener#onLongClick(android.view.View)}
 * when the view has been long clicked by the user.
 * </p>
 * <p>
 * The annotation value should be one or several of R.id.* fields. If not set,
 * the method name will be used as the R.id.* field name.
 * </p>
 * <p>
 * The method may return a <code>boolean</code>, void, or a
 * {@link java.lang.Boolean}. If returning void, it will be considered as
 * returning true (ie: the method has handled the event).
 * </p>
 * <p>
 * The method MAY have one parameter:
 * </p>
 * <ul>
 * <li>A {@link android.view.View} (or a subclass) parameter to know which view
 * has been long clicked</li>
 * </ul>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;LongClick(<b>R.id.myButton</b>)
 * void longClickOnMyButton() {
 * 	// Something Here
 * }
 * 
 * &#064;LongClick
 * void <b>myButton</b>LongClicked(View view) {
 * 	// Something Here
 * }
 * 
 * &#064;LongClick
 * void <b>myButton</b>LongClicked(Button view) {
 * 	// Something Here
 * }
 * 
 * &#064;LongClick(<b>R.id.myButton</b>)
 * boolean longClick<b>Consumed</b>OnMyButton(View view) {
 * 	return true;
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Touch
 * @see Click
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface LongClick {

	/**
	 * The R.id.* fields which refer to the Views.
	 * 
	 * @return the ids of the Views
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource names as strings which refer to the Views.
	 * 
	 * @return the resource names of the Views
	 */
	String[] resName() default "";
}
