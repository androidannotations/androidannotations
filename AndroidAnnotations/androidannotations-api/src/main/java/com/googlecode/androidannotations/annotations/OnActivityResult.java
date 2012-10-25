/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.content.Intent;

/**
 * <blockquote>
 * 
 * This annotation is intended to be used on a method to receive results from a
 * previously started activity using
 * {@link android.app.Activity#startActivityForResult(Intent, int)}
 * 
 * </blockquote> <blockquote>
 * 
 * The annotation value must be a resource id that represents the
 * <b>requestCode</b> associated with the given result. This id can be declared
 * into xml values as follows :
 * 
 * <b>&lt;item type="id" name="myRequest"/&gt;</b>
 * 
 * </blockquote> <blockquote>
 * 
 * The method may have multiple parameters :
 * <ul>
 * <li>A android.content.Intent that contains data returned by the previously
 * launched activity
 * <li>An int or an java.lang.Integer to get the resultCode.
 * 
 * Some usage examples of &#064;OnActivityResult annotation : <blockquote>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult(<b>R.id.myRequest</b>)
 * void onResult(int resultCode, Intent data) {
 * 	// Use resultCode and data
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult(<b>R.id.myRequest</b>)
 * void onResult(int resultCode) {
 * 	// Only use resultCode 
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult(<b>R.id.myRequest</b>)
 * void onResult(Intent data) {
 * 	// Only use data
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult
 * void <b>myRequest</b>Result() {
 * 	// The method name contains the res id
 * }
 * </pre>
 * 
 * </blockquote> <blockquote>
 * 
 * <pre>
 * &#064;OnActivityResult(<b>resName = &quot;myRequest&quot;</b>)
 * void anotherResult() {
 * 	// Usually for library projects
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see android.app.Activity#startActivityForResult(Intent, int)
 * @see android.app.Activity#onActivityResult(int, int, Intent)
 * @see android.app.Activity#RESULT_CANCELED
 * @see android.app.Activity#RESULT_OK
 * 
 * 
 * @author Mathieu Boniface < mat.boniface@gmail.com >
 * 
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnActivityResult {

	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";

}
