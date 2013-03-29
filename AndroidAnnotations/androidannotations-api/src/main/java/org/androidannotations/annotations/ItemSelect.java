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
 * Should be used on item selected listener methods for AdapterView classes
 * 
 * The method may have 1 or 2 parameters. The first parameter must be a boolean,
 * and the second is the object from the adapter, at the selected position. It
 * may be of any type, so be careful about potential ClassCastException.
 * 
 * If the second parameter is an int, it will be the position instead of the object from the adapter.
 * 
 * The first boolean parameter indicates if something has been selected or not.
 * If nothing was selected, the second parameter will be null.
 * 
 * The annotation value should be one of R.id.* fields. If not set, the method
 * name will be used as the R.id.* field name.
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ItemSelect {
	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";
}
