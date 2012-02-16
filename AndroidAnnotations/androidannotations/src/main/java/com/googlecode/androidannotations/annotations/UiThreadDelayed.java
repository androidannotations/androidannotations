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
 * <b>This annotation is deprecated. You should use {@link UiThread} with the delay parameter instead</b>
 * 
 * Should be used on method that must be run in the Ui thread, after the
 * specified amount of time elapses.
 * 
 * The annotation value is the delay (in milliseconds) until the method will be
 * executed.
 * 
 * 
 */
@Deprecated
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface UiThreadDelayed {
	long value();
}