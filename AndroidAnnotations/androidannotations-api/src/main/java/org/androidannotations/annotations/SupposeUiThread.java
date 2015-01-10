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
 * 		// if this method will be called from a background thread an exception
 * 		// will be thrown
 * 	}
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see org.androidannotations.api.BackgroundExecutor#setWrongThreadListener(org.androidannotations.api.BackgroundExecutor.WrongThreadListener)
 * @see org.androidannotations.api.BackgroundExecutor#DEFAULT_WRONG_THREAD_LISTENER
 * @see org.androidannotations.api.BackgroundExecutor#checkUiThread()
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SupposeUiThread {
}
