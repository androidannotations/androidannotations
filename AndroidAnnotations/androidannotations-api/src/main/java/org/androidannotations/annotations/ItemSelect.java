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
 * This annotation is intended to be used on methods to receive events defined
 * by {@link
 * android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.
 * widget. AdapterView<?>, android.view.View, int, long)} when a list item has
 * been selected by the user.
 * <p/>
 * The annotation value should be one or several of R.id.* fields. If not set,
 * the method name will be used as the R.id.* field name.
 * <p/>
 * The method MAY have one or two parameters :
 * <ul>
 * <li>A <code>boolean</code> to know if the item selected or not</li>
 * <li>An <code>int</code> parameter to know the position of the long clicked
 * item. Or, a parameter of the type of the Adapter linked to the listview.</li>
 * </ul>
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;ItemSelect(R.id.myList)
 * public void itemSelectedOnMyList() {
 * 	// ...
 * }
 * 
 * &#064;ItemSelect(R.id.myList)
 * public void myListItemPositionSelected(int position) {
 * 	// ...
 * }
 * 
 * &#064;ItemSelect
 * public void myListItemSelect(MyItem clickedItem) {
 * 	// ...
 * }
 * 
 * &#064;ItemSelect(R.id.myList)
 * public void myListItemPositionSelected(boolean selected, int position) {
 * 	// ...
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see ItemClick
 * @see ItemLongClick
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ItemSelect {
	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";
}
