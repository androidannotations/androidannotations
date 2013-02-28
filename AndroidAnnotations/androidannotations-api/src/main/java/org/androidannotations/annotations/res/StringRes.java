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
package org.androidannotations.annotations.res;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.androidannotations.annotations.ResId;

/**
 * Use on fields in activity classes that should be injected with values from
 * R.string.*
 * 
 * The annotated field must be a String
 * 
 * The annotation value must be one of R.string.* fields. If the value is not
 * set, the field name will be used as the R.string.* field name.
 * 
 * Notice that we named it @StringResValue instead of @StringValue because the
 * StringValue class already exists in java.lang and does not need any import to
 * be used.
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface StringRes {

	int value() default ResId.DEFAULT_VALUE;

	String resName() default "";
}
