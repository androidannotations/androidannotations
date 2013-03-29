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

/**
 * This annotation is intended to be used on methods to receive events defined
 * by
 * {@link android.text.TextWatcher#onTextChanged(CharSequence s, int start, int before, int count)}
 * when the text is changed on the targeted TextView or subclass of TextView.
 * 
 * The annotation value should be one or several R.id.* fields that refers to
 * TextView or subclasses of TextView. If not set, the method name will be used
 * as the R.id.* field name.
 * 
 * The method may have multiple parameter :
 * <ul>
 * <li>A {@link android.widget.TextView} parameter to know which view has
 * targeted this event
 * <li>An java.lang.CharSequence parameter to get the modified text.
 * <li>An int parameter named start to get the start position of the modified
 * text.
 * <li>An int parameter named before to know the text length before
 * modification.
 * <li>An int parameter named count to know the number of modified characters.
 * </ul>
 * 
 * Some usage examples of &#064;BeforeTextChange annotation: <blockquote>
 * 
 * <pre>
 * &#064;TextChange(<b>R.id.helloTextView</b>)
 * void onTextChangesOnHelloTextView(CharSequence text, TextView hello, int before, int start, int count) {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;TextChange
 * void <b>helloTextView</b>TextChanged(TextView hello) {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;TextChange(<b>{R.id.editText, R.id.helloTextView}</b>)
 * void onTextChangesOnSomeTextViews(TextView tv, CharSequence text) {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;TextChange(<b>R.id.helloTextView</b>)
 * void onTextChangesOnHelloTextView() {
 * 	// Something Here
 * }
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface TextChange {

	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";

}
