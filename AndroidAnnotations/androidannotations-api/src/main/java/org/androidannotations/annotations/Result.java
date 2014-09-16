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

import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Use on any native, {@link Parcelable} or {@link Serializable} parameter of an
 * {@link OnActivityResult} annotated method to bind it with the value from the Intent.
 * </p>
 * <p>
 * The annotation value is the key used for the result data. If not set, the field name
 * will be used as the key.
 * </p>
 *
 * <blockquote>
 *
 * Some usage examples of &#064;Result annotation:
 *
 * <pre>
 * &#064;OnActivityResult(REQUEST_CODE)
 * void onResult(int resultCode, Intent data, <b>@Result String value</b>) {
 * }
 *
 * &#064;OnActivityResult(REQUEST_CODE)
 * void onResult(int resultCode, <b>@Result(value = "key") String value</b>) {
 * }
 *
 * &#064;OnActivityResult(REQUEST_CODE)
 * void onResult(<b>@Result String strVal</b>, <b>@Result int intVal</b>) {
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see android.app.Activity#onActivityResult(int, int, Intent)
 * @see OnActivityResult
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PARAMETER)
public @interface Result {
	String value() default "";
}
