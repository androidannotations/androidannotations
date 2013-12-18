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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Should be used on method that must be run in a background thread.
 * <p/>
 * The annotated method MUST return void and MAY contain parameters.
 * <p/>
 * The generated code is based on {@link BackgroundExecutor} methods.
 * 
 * 
 * <h2>Cancellation</h2>
 * Since 3.0, you're able to cancel a background task by calling
 * <code>BackgroundExecutor.cancelAll("id")</code> where "id" matches the
 * {@link Background#id()} value.
 * 
 * <p/>
 * <blockquote>
 * 
 * <b>Example</b> :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 	private static final String TASK_ID = &quot;task1&quot;;
 * 
 * 	&#064;Background(id = TASK_ID)
 * 	void launchTask() {
 * 		// ...
 * 	}
 * 
 * 	void stopTask() {
 * 		BackgroundExecutor.cancelAll(TASK_ID);
 * 	}
 * 
 * }
 * </pre>
 * 
 * </blockquote>
 * <p/>
 * <b>Note</b>: Cancellation may or may not be successful. If the task wasn't
 * executed yet, it will be removed from the pool. But i could fail if task has
 * already completed, has already been cancelled, or could not be cancelled for
 * some other reason. See {@link Future#cancel(boolean)} for more information.
 * 
 * 
 * <h2>Execution flow</h2>
 * By default, all tasks will be put in a {@link ScheduledThreadPoolExecutor}
 * with a core pool size of <code>2 * numberOfCpu</code>. Which means that
 * background methods will be executed in <b>PARALLEL</b>. You can change this
 * by calling <code>BackgroundExecutor.setExecutor(...)</code>.
 * 
 * If you want execute ALL background methods SEQUENTIALLY, the best way is to
 * change the executor of {@link BackgroundExecutor} to a
 * {@link ScheduledThreadPoolExecutor} with a core pool size of <code>1</code>.
 * <p/>
 * If you want execute some background methods SEQUENTIALLY, you should simply
 * use {@link Background#serial()} field. All task with the same serial key will
 * be executed sequentially.
 * 
 * <p/>
 * <blockquote>
 * 
 * <b>Example 1</b> (all tasks executed sequentially) :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	static {
 * 		BackgroundExecutor.setExecutor(Executors.newScheduledThreadPool(1));
 * 	}
 * 
 * 	private int i = 0;
 * 
 * 	void launchTasks() {
 * 		backgroundTask();
 * 		backgroundTask();
 * 		backgroundTask();
 * 	}
 * 
 * 	&#064;Background
 * 	void backgroundTask() {
 * 		Log.i(&quot;AA&quot;, &quot;i = &quot;, i++);
 * 	}
 * }
 * </pre>
 * 
 * <b>Example 2</b> (some tasks executed sequentially) :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	private int i = 0;
 * 
 * 	void launchTasks() {
 * 		backgroundTask();
 * 		backgroundTask();
 * 		backgroundTask();
 * 	}
 * 
 * 	&#064;Background(serial = &quot;sequence1&quot;)
 * 	void backgroundTask() {
 * 		Log.i(&quot;AA&quot;, &quot;i = &quot;, i++);
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * <h2>Delay</h2>
 * Sometimes you may want to delay execution of a background method. To do so,
 * you should use the {@link Background#delay()} field.
 * </p>
 * <b>Example</b> :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;Background(delay = 2000)
 * 	void backgroundTask() {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * @see UiThread
 * @see BackgroundExecutor
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

	/**
	 * Minimum delay, in milliseconds, before the background task is executed.
	 */
	int delay() default 0;

	/**
	 * Serial execution group.
	 * 
	 * All background tasks having the same <code>serial</code> will be executed
	 * sequentially.
	 **/
	String serial() default "";
}
