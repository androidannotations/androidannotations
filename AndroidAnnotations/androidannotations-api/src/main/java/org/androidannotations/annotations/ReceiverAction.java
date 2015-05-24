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
 * Should be used on a method that must respond to a specific action in an
 * {@link EReceiver} annotated class.
 * </p>
 * <p>
 * The class MAY contain several {@link ReceiverAction} annotated methods.
 * </p>
 * <p>
 * The method annotated with {@link ReceiverAction} may have as parameters :
 * </p>
 * <ul>
 * <li>A {@link android.content.Context} which will be the context given in
 * {@code void onReceive(Context context, Intent intent)}</li>
 * <li>A {@link android.content.Intent} which will be the intent given in
 * {@code void onReceive(Context context, Intent intent)}</li>
 * <li>Any native, {@link android.os.Parcelable} or {@link java.io.Serializable}
 * parameters annotated with {@link ReceiverAction.Extra} which will be the
 * extra put in the intent. The key of this extra is the value of the annotation
 * {@link ReceiverAction.Extra} if set or the name of the parameter.</li>
 * </ul>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EReceiver
 * public class MyReceiver extends BroadcastReceiver {
 * 
 * 	&#064;ReceiverAction
 * 	void mySimpleAction(Intent intent) {
 * 		// ...
 * 	}
 * 
 * 	&#064;ReceiverAction
 * 	void myAction(@ReceiverAction.Extra String valueString, Context context) {
 * 		// ...
 * 	}
 * 
 * 	&#064;ReceiverAction
 * 	void anotherAction(@ReceiverAction.Extra(&quot;specialExtraName&quot;) String valueString, @ReceiverAction.Extra long valueLong) {
 * 		// ...
 * 	}
 * 
 * 	&#064;Override
 * 	public void onReceive(Context context, Intent intent) {
 * 		// empty, will be overridden in generated subclass
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * Note: Since
 * {@link android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
 * BroadcastReceiver#onReceive} is abstract, you have to add an empty
 * implementation. For convenience, we provide the
 * {@link org.androidannotations.api.support.content.AbstractBroadcastReceiver
 * AbstractBroadcastReceiver} class, which implements that method, so you do not
 * have to do in your actual class if you derive it.
 * </p>
 * 
 * @see EReceiver
 * @see org.androidannotations.api.support.content.AbstractBroadcastReceiver
 *      AbstractBroadcastReceiver
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ReceiverAction {

	/**
	 * Define a set of actions this method should handle.
	 * 
	 * @return the actions
	 */
	String[] actions();

	/**
	 * Define a set of data schemes to filter the Intent. If this field isn't
	 * all schemes are allowed
	 *
	 * @return the data schemes to filter
	 */
	String[] dataSchemes() default {};

	/**
	 * <p>
	 * Should be used on any native, {@link android.os.Parcelable} or
	 * {@link java.io.Serializable} parameter of a method annotated with
	 * {@link ReceiverAction} to inject the extra put in the intent parameter of
	 * {@code void onReceive(Context context, Intent intent)}. The key of this
	 * extra is the value of the annotation {@link ReceiverAction.Extra} if it
	 * is set or the name of the parameter.
	 * </p>
	 */
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.PARAMETER)
	public @interface Extra {

		/**
		 * Define the extra's name. If this parameter isn't set the annotated
		 * parameter name will be used.
		 *
		 * @return the extra's name
		 */
		String value() default "";
	}
}
