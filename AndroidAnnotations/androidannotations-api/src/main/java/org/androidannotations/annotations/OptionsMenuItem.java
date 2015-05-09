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
 * Use it on android.app.Fragment or android.support.v4.app.Fragment fields in
 * activity classes to inject a menu item.
 * </p>
 * <p>
 * The field MUST be of type {@link android.view.MenuItem}
 * </p>
 * <p>
 * The annotation value should be one or several of R.id.* fields. If not set,
 * the method name will be used as the R.id.* field name.
 * </p>
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
 * 	&#064;OptionsMenuItem
 * 	MenuItem menuRefresh;
 * 
 * 	&#064;OptionsMenuItem(R.id.menu_search)
 * 	MenuItem menuItemSearch;
 * }
 * </pre>
 * 
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface OptionsMenuItem {

	/**
	 * The R.id.* fields which refers to the menu items.
	 * 
	 * @return the ids of the menus
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource names as strings which refers to the menu items.
	 * 
	 * @return the resource names of the menus
	 */
	String[] resName() default "";

}
