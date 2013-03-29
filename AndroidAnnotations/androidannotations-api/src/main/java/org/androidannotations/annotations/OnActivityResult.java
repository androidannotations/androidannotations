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

import android.content.Intent;

/**
 * <blockquote>
 * 
 * This annotation is intended to be used on methods to receive results from a
 * previously started activity using
 * {@link android.app.Activity#startActivityForResult(Intent, int)}
 * 
 * </blockquote> <blockquote>
 * 
 * The annotation value must be an integer constant that represents the
 * <b>requestCode</b> associated with the given result.
 * 
 * </blockquote> <blockquote>
 * 
 * The method may have multiple parameter :
 * <ul>
 * <li>A android.content.Intent that contains data
 * <li>An int or an java.lang.Integer to get the resultCode
 * </ul>
 * 
 * Some usage examples of &#064;OnActivityResult annotation: <blockquote>
 * 
 * </blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult(<b>REQUEST_CODE</b>)
 * void onResult(int resultCode, Intent data) {
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult(<b>REQUEST_CODE</b>)
 * void onResult(int resultCode) {
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult(<b>ANOTHER_REQUEST_CODE</b>)
 * void onResult(Intent data) {
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult(<b>ANOTHER_REQUEST_CODE</b>)
 * void onResult() {
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see android.app.Activity#startActivityForResult(Intent, int)
 * @see android.app.Activity#onActivityResult(int, int, Intent)
 * 
 * 
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnActivityResult {

	int value();

}
