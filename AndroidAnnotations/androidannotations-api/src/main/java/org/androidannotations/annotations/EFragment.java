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
 * Should be used on Fragment classes to enable usage of AndroidAnnotations
 * 
 * The annotation value should be one of R.layout.* fields, or none if you want
 * to handle the view creation by yourself.
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EFragment {
	int value() default ResId.DEFAULT_VALUE;

	String resName() default "";
}
