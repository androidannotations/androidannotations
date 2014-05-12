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
 * Should be used on editor action listener methods in enhanced classes with view support
 * 
 * The method may have some parameters amoung :
 *  - a TextView
 *  - an int : the actionId
 *  - a KeyEvent
 * 
 * The annotation value should be one of R.id.* fields. If not set, the method
 * name will be used as the R.id.* field name.
 * 
 */

/**
 * This annotation is intended to be used on methods to receive events defined
 * by
 * {@link android.widget.TextView.OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent)}
 * when an action is performed on the editor.
 * <p/>
 * The annotation value should be one or several R.id.* fields that refers to
 * TextView or subclasses of TextView. If not set, the method name will be used
 * as the R.id.* field name.
 * <p/>
 * The method MAY have multiple parameter :
 * <ul>
 * <li>A {@link android.widget.TextView} parameter to know which view has
 * targeted this event
 * <li>An int parameter to get the actionId
 * <li>A {@link android.view.KeyEvent} parameter
 * </ul>
 * <p/>
 * <blockquote>
 *
 * Examples :
 *
 * <pre>
 * &#064;EditorAction(<b>R.id.helloTextView</b>)
 * void onEditorActionsOnHelloTextView(TextView hello, int actionId, KeyEvent keyEvent) {
 * 	// Something Here
 * }
 *
 * &#064;EditorAction
 * void <b>helloTextView</b>EditorAction(TextView hello) {
 * 	// Something Here
 * }
 *
 * &#064;EditorAction(<b>{R.id.editText, R.id.helloTextView}</b>)
 * void onEditorActionsOnSomeTextViews(TextView tv, int actionId) {
 * 	// Something Here
 * }
 *
 * &#064;EditorAction(<b>R.id.helloTextView</b>)
 * void onEditorActionsOnHelloTextView() {
 * 	// Something Here
 * }
 * </pre>
 *
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface EditorAction {

	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";

}
