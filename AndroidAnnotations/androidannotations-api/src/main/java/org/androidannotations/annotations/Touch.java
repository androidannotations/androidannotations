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
 * Should be used on touch listener methods in activity classes
 * 
 * The method may have one or two parameters, the first parameter must be a
 * android.view.MotionEvent and the second one must be a android.view.View.
 * 
 * The method may return a boolean, void, or a java.lang.Boolean. If returning
 * void, it will be considered as returning true (ie the method has handled the
 * event).
 * 
 * The annotation value should be one of R.id.* fields. If not set, the method
 * name will be used as the R.id.* field name.
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Touch {
	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";
}
