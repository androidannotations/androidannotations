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

/**
 * <p>
 * Should be used on method that must be run in the Ui thread
 * </p>
 * <p>
 * The annotated method MUST return void and MAY contain parameters.
 * </p>
 * <p>
 * The generated code is based on a local {@link android.os.Handler} instance.
 * </p>
 * 
 * 
 * <h2>Delay</h2>
 * <p>
 * Sometimes you may want to delay execution of a Ui thread method. To do so,
 * you should use the {@link #delay()} field.
 * </p>
 * <blockquote> <b>Example</b> :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;UiThread(delay = 2000)
 * 	void uiThreadTask() {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * <h2>Execution flow</h2>
 * <p>
 * Prior to 3.0, {@link UiThread} annotated method calls was always added in the
 * handler execution queue to ensure that execution was done in Ui thread. In
 * 3.0, we kept the same behavior for compatibility purpose.
 * </p>
 * <p>
 * But, if you want to optimize UiThread calls, you may want to change
 * {@link #propagation()} value to <code>REUSE</code>. In this configuration,
 * the code will make a direct call to the method if current thread is already
 * Ui thread. If not, we're falling back to handler call.
 * </p>
 * <blockquote> <b>Example</b> :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;UiThread
 * 	void uiThreadTask() {
 * 		// This code is executed via the handler
 * 		// The following method will be directly executed instead of using
 * 		// handler
 * 		uiThreadTaskReused();
 * 	}
 * 
 * 	&#064;UiThread(propagation = REUSE)
 * 	void uiThreadTaskReused() {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * <h2>Cancellation</h2>
 * <p>
 * You can cancel UiThread tasks if you provide an id (which cannot be an empty
 * string) with the {@link #id()} parameter. Please note tasks which use
 * <code>REUSE</code> {@link #propagation()} cannot have an id hence cannot be
 * cancelled. To cancel all {@link UiThread} tasks with a given id, call
 * {@link org.androidannotations.api.UiThreadExecutor#cancelAll(String)
 * UiThreadExecutor#cancelAll(String)}.
 * </p>
 * 
 * <blockquote> <b>Example</b> :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;UiThread(id = &quot;myId&quot;)
 * 	void uiThreadTask() {
 * 		// do sg
 * 	}
 * }
 * 
 * ...
 * 
 * UiThreadExecutor.cancelAll(&quot;myId&quot;);
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * @see Background
 * @see android.os.Handler
 * @see org.androidannotations.api.UiThreadExecutor#cancelAll(String)
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface UiThread {

	/**
	 * The delay of the execution in milliseconds.
	 * 
	 * @return the delay of the execution
	 */
	long delay() default 0;

	/**
	 * If propagation is {@link Propagation#REUSE}, the method will check first
	 * if it is inside the UI thread already. If so, it will directly call the
	 * method instead of using the handler. The default value is
	 * {@link Propagation#ENQUEUE}, which will always call the handler.
	 * 
	 * @return {@link Propagation#ENQUEUE} to always call the handler,
	 *         {@link Propagation#REUSE}, to check whether it is already on the
	 *         UI thread
	 */
	Propagation propagation() default Propagation.ENQUEUE;

	/**
	 * Indicates the propagation behavior of the UiThread annotated method.
	 */
	public enum Propagation {

		/**
		 * The method will always call the Handler.
		 */
		ENQUEUE, //
		/**
		 * The method will check first if it is inside the UI thread already.
		 */
		REUSE
	}

	/**
	 * Identifier for cancellation.
	 * 
	 * To cancel all tasks having a specified id:
	 * 
	 * <pre>
	 * UiThreadExecutor.cancelAll(&quot;my_background_id&quot;);
	 * </pre>
	 * 
	 * @return the id for cancellation
	 */
	String id() default "";
}
