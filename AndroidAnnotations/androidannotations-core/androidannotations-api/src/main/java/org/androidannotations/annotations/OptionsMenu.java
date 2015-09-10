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
 * Should be used on {@link EActivity} or {@link EFragment} annotated classes to
 * inject one or multiple menus.
 * </p>
 * <p>
 * The annotation value should be one or several R.menu.* fields.
 * </p>
 * <p>
 * <b>Note:</b> Fragment menus are compounds with parent Activity menus
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * &#064;OptionsMenu({ R.menu.my_menu1, R.menu.my_menu2 })
 * public class MyActivity extends Activity {
 * 
 * }
 * 
 * &#064;EFragment
 * &#064;OptionsMenu(R.menu.my_fragment_menu)
 * public class MyFragment extends Fragment {
 * 
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see OptionsItem
 * @see OptionsMenuItem
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface OptionsMenu {

	/**
	 * The R.menu.* fields which refers to the menus.
	 * 
	 * @return the ids of the menus
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource names as strings which refers to the menus.
	 * 
	 * @return the resource names of the menus
	 */
	String[] resName() default "";
}
