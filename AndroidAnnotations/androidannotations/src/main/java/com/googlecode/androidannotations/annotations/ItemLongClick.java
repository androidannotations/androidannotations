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

/**
 * Should be used on item long click listener methods for AdapterView classes
 * 
 * The method may have 0 or 1 parameter, that will be the object from the
 * adapter, at the selected position. It may be of any type, so be careful about
 * potential ClassCastException.
 * 
 * If the parameter is an int, it will be the position instead of the object from the adapter.
 * 
 * The annotation value should be one of R.id.* fields. If not set, the method
 * name will be used as the R.id.* field name..
 * 
 * The method may return a boolean, void, or a java.lang.Boolean. If returning
 * void, it will be considered as returning true (ie the method has handled the
 * event).
 * 
 * @author Benjamin Fellous
 * @author Pierre-Yves Ricau
 * @author Mathieu Boniface
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ItemLongClick {
	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";
}
