/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
 * <p>
 * This annotation is intended to be used on methods to log at runtime the
 * execution time.
 * </p>
 * <p>
 * Since <i>AndroidAnnotations 3.1</i> log messages contain the method parameter
 * and return values.
 * </p>
 * <p>
 * All annotation values are optional :
 * </p>
 * <ul>
 * <li><i>tag</i>: the tag used for the log message. (default: enclosing class
 * name)</li>
 * <li><i>level</i>: the log level used for the log message. (default :
 * LOG.INFO).</li>
 * </ul>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;Trace
 * void doWork() {
 * 	// ... Do Work ...
 * }
 * 
 * &#064;Trace
 * boolean doMoreWork(String someString) {
 * 	// ... Do more Work ...
 * }
 * </pre>
 * 
 * This will log these lines :
 * 
 * <pre>
 * I/TracedMethodActivity(  302): Entering [void doWork() ]
 * I/TracedMethodActivity(  302): Exiting [void doWork() ], duration in ms: 1002
 * I/TracedMethodActivity(  302): Entering [boolean doMoreWork(someString = Hello World)]
 * I/TracedMethodActivity(  302): Exiting [boolean doMoreWork(String) returning: true], duration in ms: 651
 * </pre>
 * 
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Trace {

	/**
	 * The string indicating that no tag was given for the log message.
	 */
	String DEFAULT_TAG = "NO_TAG";

	/**
	 * The tag used for the log message.
	 * 
	 * @return the tag of the message
	 */
	String tag() default DEFAULT_TAG;

	/**
	 * The log level used for the log message.
	 * 
	 * @return the logging level of the message
	 */
	int level() default Log.INFO;

}
