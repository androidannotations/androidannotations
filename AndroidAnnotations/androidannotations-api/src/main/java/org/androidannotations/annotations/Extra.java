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
 * Use on Extra fields in activity and service classes. This String value field
 * corresponds to the Android extra name.
 * 
 * When {@link Extra} is used on fields in an Activity, the intent builder will
 * hold dedicated methods for these extras. Calling
 * Activity#setIntent(android.content.Intent) will automatically update the
 * annotated extras.
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Extra {

	String value() default "";
}
