package org.androidannotations.annotations;

import org.androidannotations.api.BackgroundExecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ensures that the method is called from the background thread with (optionally) restrictions by allowed serials.
 * If it is not called from a supposed background thread, then {@link IllegalStateException}
 * will be thrown (by default).
 * <p/>
 * <blockquote> <b>Example</b> :
 * <p/>
 * <pre>
 * &#064;EBean
 * public class MyBean {
 *
 * 	&#064;SupposeBackground
 * 	boolean someMethodThatShouldNotBeCalledFromUiThread() {
 * 		//if this method will be called from the UI-thread an exception will be thrown
 *    }
 *
 * 	&#064;SupposeBackground(serial = {"serial1", "serial2"})
 * 	boolean someMethodThatShouldBeCalledFromSerial1OrSerial2() {
 * 		//if this method will be called from another thread then a background thread with a
 * 		//serial "serial1" or "serial2", an exception will be thrown
 *    }
 *
 * }
 * </pre>
 * <p/>
 * </blockquote>
 *
 * @see BackgroundExecutor#setWrongThreadListener(BackgroundExecutor.WrongThreadListener)
 * @see BackgroundExecutor#DEFAULT_WRONG_THREAD_LISTENER
 * @see BackgroundExecutor#checkBgThread(String...)
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SupposeBackground {

	/**
	 * Allowed serials to restrict a calling thread. If it is an empty list, then any background thread is allowed.
	 *
	 * @see BackgroundExecutor#checkBgThread(String...)
	 */
	String[] serial() default {};

}
