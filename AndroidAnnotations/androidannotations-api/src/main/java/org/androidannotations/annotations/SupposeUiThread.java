package org.androidannotations.annotations;

import org.androidannotations.api.BackgroundExecutor;

import java.lang.IllegalStateException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ensures that method is called from the UI thread. If it is not, then
 * {@link IllegalStateException} will be thrown (by default).
 *
 * <blockquote> <b>Example</b> :
 *
 * <pre>
 * &#064;EBean
 * public class MyBean {
 *
 * 	&#064;SupposeUiThread
 * 	boolean someMethodThatShouldBeCalledOnlyFromUiThread() {
 * 		//if this method will be called from a background thread an exception will be thrown
 *    }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see BackgroundExecutor#setWrongThreadListener(BackgroundExecutor.WrongThreadListener)
 * @see BackgroundExecutor#DEFAULT_WRONG_THREAD_LISTENER
 * @see BackgroundExecutor#checkUiThread()
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SupposeUiThread {
}
