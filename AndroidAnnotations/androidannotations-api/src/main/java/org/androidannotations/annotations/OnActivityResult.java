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
 * This annotation is intended to be used on methods to receive results from a
 * previously started activity using
 * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)
 * Activity#startActivityForResult(Intent, int)} or the generated
 * <code>IntentBuilder.startActivityForResult()</code> method of the activity.
 * </p>
 * <p>
 * The annotation value must be an integer constant that represents the
 * <b>requestCode</b> associated with the given result.
 * </p>
 * <p>
 * The method may have multiple parameter :
 * </p>
 * <ul>
 * <li>A {@link android.content.Intent Intent} that contains data</li>
 * <li>An <code>int</code> or an {@link java.lang.Integer Integer} to get the
 * resultCode</li>
 * <li>Any native, {@link android.os.Parcelable Parcelable} or
 * {@link java.io.Serializable Serializable} parameter annotated with
 * {@link org.androidannotations.annotations.OnActivityResult.Extra
 * OnActivityResult.Extra} to get an object put in the extras of the intent.</li>
 * </ul>
 *
 * <blockquote>
 * 
 * Some usage examples of &#064;OnActivityResult annotation:
 * 
 * <pre>
 * &#064;OnActivityResult(<b>REQUEST_CODE</b>)
 * void onResult(int resultCode, Intent data) {
 * }
 * 
 * &#064;OnActivityResult(<b>REQUEST_CODE</b>)
 * void onResult(int resultCode) {
 * }
 * 
 * &#064;OnActivityResult(<b>ANOTHER_REQUEST_CODE</b>)
 * void onResult(Intent data) {
 * }
 * 
 * &#064;OnActivityResult(<b>ANOTHER_REQUEST_CODE</b>)
 * void onResult(&#064;OnActivityResult.Extra anExtra) {
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see EActivity
 * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnActivityResult {

	/**
	 * The <b>requestCode</b> associated with the given result.
	 * 
	 * @return the requestCode
	 */
	int value();

	/**
	 * <p>
	 * Use on any native, {@link android.os.Parcelable} or
	 * {@link java.io.Serializable} parameter of an {@link OnActivityResult}
	 * annotated method to bind it with the value from the Intent.
	 * </p>
	 * <p>
	 * The annotation value is the key used for the result data. If not set, the
	 * field name will be used as the key.
	 * </p>
	 *
	 * <blockquote>
	 *
	 * Some usage examples of &#064;Result annotation:
	 *
	 * <pre>
	 * &#064;OnActivityResult(REQUEST_CODE)
	 * void onResult(int resultCode, Intent data, <b>@Extra String value</b>) {
	 * }
	 * 
	 * &#064;OnActivityResult(REQUEST_CODE)
	 * void onResult(int resultCode, <b>@Extra(value = "key") String value</b>) {
	 * }
	 * 
	 * &#064;OnActivityResult(REQUEST_CODE)
	 * void onResult(<b>@Extra String strVal</b>, <b>@Extra int intVal</b>) {
	 * }
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 * @see OnActivityResult
	 */

	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.PARAMETER)
	public @interface Extra {

		/**
		 * They key of the result data.
		 * 
		 * @return the key
		 */
		String value() default "";
	}

}
