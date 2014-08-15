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

/**
 * <p>
 * Should be used on a method that must respond to a specific action in an
 * {@link EReceiver} annotated class. The method name will be used as action
 * name unless the {@link #value()} field is set.
 * </p>
 * <p>
 * The method could contain any type or parameters.
 * </p>
 * <p>
 * The class MAY contain several {@link ReceiverAction} annotated methods.
 * </p>
 * <p>
 * You MAY use the {@link ReceiverAction.Extra} annotation on parameters to
 * define a different extra name.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EReceiver
 * public class MyIntentService extends BroadcastReceiver {
 * 
 * 	&#064;ReceiverAction
 * 	void mySimpleAction() {
 * 		// ...
 * 	}
 * 
 * 	&#064;ReceiverAction
 * 	void myAction(String valueString, long valueLong) {
 * 		// ...
 * 	}
 * 
 * 	&#064;ReceiverAction
 * 	void anotherAction(@ReceiverAction.Extra(&quot;specialExtraName&quot;) String valueString, long valueLong) {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see EIntentService
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ReceiverAction {

	/**
	 * Define the action's name. If this field isn't set the annotated method
	 * name will be used.
	 *
	 * @return the action's name
	 */
	String value() default "";

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
