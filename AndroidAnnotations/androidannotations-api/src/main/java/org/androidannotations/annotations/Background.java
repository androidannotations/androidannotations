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
 * Should be used on method that must be run in a background thread. This method
 * must belong to an activity annotated with @Layout.
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Background {
	/**
	 * Identifier for task cancellation.
	 * 
	 * To cancel all tasks having a specified background id:
	 * 
	 * <pre>
	 * boolean mayInterruptIfRunning = true;
	 * BackgroundExecutor.cancelAll(&quot;my_background_id&quot;, mayInterruptIfRunning);
	 * </pre>
	 **/
	String id() default "";

	/** Minimum delay, in milliseconds, before the background task is executed. */
	int delay() default 0;

	/**
	 * Serial execution group.
	 * 
	 * All background tasks having the same <code>serial</code> will be executed
	 * sequentially.
	 **/
	String serial() default "";
}
