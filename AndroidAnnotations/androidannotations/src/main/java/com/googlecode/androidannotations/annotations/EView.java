/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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

/**
 * Should be used on custom classes that extend View to enable usage of
 * AndroidAnnotations
 * 
 * Any view related code should happen in an {@link AfterViews} annotated
 * method.<br>
 * <br>
 * 
 * Supported annotations in @EView :
 * 
 * <ul>
 * <li>ViewById</li>
 * <li>AfterViews</li>
 * <li>Click</li>
 * <li>ItemClick</li>
 * <li>ItemLongClick</li>
 * <li>ItemSelected</li>
 * <li>LongClick</li>
 * <li>Touch</li>
 * </ul>
 * 
 * @see <a
 *      href="http://developer.android.com/guide/topics/ui/custom-components.html">How
 *      to build a custom component.</a>
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface EView {
}