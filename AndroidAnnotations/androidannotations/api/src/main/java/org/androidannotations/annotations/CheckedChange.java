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
 * {@link android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)}
 * after the checked state is changed on the targeted CompoundButton or subclass
 * of CompoundButton.
 * </p>
 * <p>
 * The annotation value should be one or several R.id.* fields that refers to
 * CompoundButton or subclasses of CompoundButton. If not set, the method name
 * will be used as the R.id.* field name.
 * </p>
 * <p>
 * The method MAY have multiple parameter:
 * </p>
 * <ul>
 * <li>A {@link android.widget.CompoundButton} (or a subclass) parameter to know
 * which view has targeted this event</li>
 * <li>An {@link boolean} to know the new state of the view.</li>
 * </ul>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;CheckedChange(<b>R.id.myButton</b>)
 * void checkedChangedOnMyButton(boolean isChecked, CompoundButton button) {
 * 	// Something Here
 * }
 * 
 * &#064;CheckedChange
 * void <b>myButton</b>CheckedChanged(CompoundButton button) {
 * 	// Something Here
 * }
 * 
 * &#064;CheckedChange
 * void <b>myButton</b>CheckedChanged(CheckBox button) {
 * 	// Something Here
 * }
 * 
 * &#064;CheckedChange(<b>{R.id.myButton, R.id.myButton1}</b>)
 * void checkedChangedOnSomeButtons(CompoundButton button, boolean isChecked) {
 * 	// Something Here
 * }
 * 
 * &#064;CheckedChange(<b>R.id.myButton</b>)
 * void checkedChangedOnMyButton() {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CheckedChange {

	/**
	 * The R.id.* fields which refer to the CompoundButtons or subclasses of
	 * CompoundButton.
	 * 
	 * @return the ids of the CompoundButtons
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource names as strings which refer to the CompoundButtons or
	 * subclasses of CompoundButton.
	 * 
	 * @return the resource names of the CompoundButtons
	 */
	String[] resName() default "";
}
