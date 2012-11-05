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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.util.Log;

/**
 * Use this annotation to log at runtime the execution time of the targeted
 * method.
 * 
 * <i>tag</i> (optional) : the tag used for the log message. (default: enclosing
 * class name)
 * 
 * <i>level</i> (optional) : the log level used for the log message. (default :
 * LOG.INFO).
 * 
 * @author Mathieu Boniface
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Trace {

	public static final String DEFAULT_TAG = "NO_TAG";

	String tag() default DEFAULT_TAG;

	int level() default Log.INFO;

}
