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
 * This annotation is intended to be used on methods to receive events defined
 * by
 * {@link android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)}
 * when a list item has been clicked by the user.
 * </p>
 * <p>
 * The annotation value should be one or several of R.id.* fields. If not set,
 * the method name will be used as the R.id.* field name.
 * </p>
 * <p>
 * The method MAY have one parameter :
 * </p>
 * <ul>
 * <li>An <code>int</code> parameter to know the position of the clicked item.
 * Or, a parameter of the type of the Adapter linked to the listview.</li>
 * </ul>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;ItemClick(R.id.myList)
 * public void itemClickedOnMyList() {
 * 	// ...
 * }
 * 
 * &#064;ItemClick(R.id.myList)
 * public void myListItemPositionClicked(int position) {
 * 	// ...
 * }
 * 
 * &#064;ItemClick
 * public void myListItemClicked(MyItem clickedItem) {
 * 	// ...
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see ItemLongClick
 * @see ItemSelect
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ItemClick {

	/**
	 * The R.id.* fields which refer to the AdapterViews.
	 * 
	 * @return the ids of the AdapterViews
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource names as strings which refer to the AdapterViews.
	 * 
	 * @return the resource names of the AdapterViews
	 */
	String[] resName() default "";
}
