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

import android.text.Editable;

/**
 * This annotation is intended to be used on methods to receive events defined
 * by {@link android.text.TextWatcher#afterTextChanged(Editable s)} after the
 * text is changed on the targeted TextView or subclass of TextView.
 * <p/>
 * The annotation value should be one or several R.id.* fields that refers to
 * TextView or subclasses of TextView. If not set, the method name will be used
 * as the R.id.* field name.
 * <p/>
 * The method MAY have multiple parameter:
 * <ul>
 * <li>A {@link android.widget.TextView} parameter to know which view has
 * targeted this event
 * <li>An {@link android.text.Editable} to make changes on modified text.
 * </ul>
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;AfterTextChange(<b>R.id.helloTextView</b>)
 * void afterTextChangedOnHelloTextView(Editable text, TextView hello) {
 * 	// Something Here
 * }
 * 
 * &#064;AfterTextChange
 * void <b>helloTextView</b>AfterTextChanged(TextView hello) {
 * 	// Something Here
 * }
 * 
 * &#064;AfterTextChange(<b>{R.id.editText, R.id.helloTextView}</b>)
 * void afterTextChangedOnSomeTextViews(TextView tv, Editable text) {
 * 	// Something Here
 * }
 * 
 * &#064;AfterTextChange(<b>R.id.helloTextView</b>)
 * void afterTextChangedOnHelloTextView() {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see BeforeTextChange
 * @see TextChange
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AfterTextChange {

	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";

}
