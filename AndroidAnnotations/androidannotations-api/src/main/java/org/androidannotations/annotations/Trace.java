/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
 * This annotation is intended to be used on methods to log at runtime the
 * execution time.
 * <p/>
 * All annotation values are optional :
 * <ul>
 * <li><i>tag</i>: the tag used for the log message. (default: enclosing class
 * name)</li>
 * <li><i>level</i>: the log level used for the log message. (default :
 * LOG.INFO).</li>
 * </ul>
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;Trace
 * void doWork() {
 * 	// ... Do Work ...
 * }
 * </pre>
 * 
 * This will log these lines :
 * 
 * <pre>
 * I/TracedMethodActivity(  302): Entering [void doWork() ]
 * I/TracedMethodActivity(  302): Exiting [void doWork() ], duration in ms: 1002
 * </pre>
 * 
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Trace {

	public static final String DEFAULT_TAG = "NO_TAG";

	String tag() default DEFAULT_TAG;

	int level() default Log.INFO;

}
