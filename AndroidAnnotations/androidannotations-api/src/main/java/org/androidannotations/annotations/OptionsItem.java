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
 * This annotation is intended to be used on methods to receive click on menu
 * items.
 * </p>
 * <p>
 * The annotation value should be one or several of R.id.* fields. If not set,
 * the method name will be used as the R.id.* field name.
 * </p>
 * <p>
 * The method may return a <code>boolean</code>, void, or a
 * {@link java.lang.Boolean}. If returning void, it will be considered as
 * returning true (ie: the method has handled the event).
 * </p>
 * <p>
 * The method MAY have one parameter:
 * </p>
 * <ul>
 * <li>A {@link android.view.MenuItem} parameter to know which menu item has
 * been clicked</li>
 * </ul>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * &#064;OptionsMenu({ R.menu.my_menu1, R.menu.my_menu2 })
 * public class MyActivity extends Activity {
 * 
 * 	&#064;OptionsItem
 * 	void menuRefreshSelected() {
 * 		// ...
 * 	}
 * 
 * 	&#064;OptionsItem({ R.id.menu_search, R.id.menu_share })
 * 	boolean multipleMenuItems() {
 * 		return false;
 * 	}
 * 
 * 	&#064;OptionsItem
 * 	void menu_add(MenuItem item) {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see OptionsMenu
 * @see OptionsMenuItem
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OptionsItem {

	/**
	 * The R.id.* field which refers to the menu item.
	 * 
	 * @return the id of the menu item
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource name which refers to the menu item.
	 *
	 * @return the resource name of the menu item
	 */
	String[] resName() default "";

}
