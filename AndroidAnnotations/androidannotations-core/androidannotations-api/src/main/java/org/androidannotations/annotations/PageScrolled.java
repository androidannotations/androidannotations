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
 * <code>android.support.v4.view.ViewPager.OnPageChangeListener.OnPageScrolled</code>
 * when the current page is scrolled, either as part of a programmatically
 * initiated smooth scroll or a user initiated touch scroll.
 * </p>
 * <p>
 * The annotation value should be one or several R.id.* fields that refers to
 * ViewPager or subclasses of ViewPager. If not set, the method name will be
 * used as the R.id.* field name.
 * </p>
 * <p>
 * The method MAY have multiple parameters, but the order must be following :
 * </p>
 * <ul>
 * <li>A ViewPager parameter to know which view has targeted this event.</li>
 * <li>An int parameter named position to get position index of the first page
 * currently being displayed. Page position+1 will be visible if positionOffset
 * is nonzero.</li>
 * <li>A float parameter is value from [0,1) indicating the offset from the page
 * at position.</li>
 * <li>An int parameter named positionOffsetPixels is value in pixels indicating
 * the offset from position.</li>
 * </ul>
 *
 * <blockquote>
 *
 * Examples :
 *
 * <pre>
 * &#064;PageScrolled(<b>R.id.viewpager</b>)
 * void onPageScrolled(ViewPager view, int position, float positionOffset, int positionOffsetPixels) {
 * 	// Something Here
 * }
 *
 * &#064;PageScrolled
 * void viewPager(ViewPager view) {
 * 	// Something Here
 * }
 *
 * &#064;PageScrolled(<b>{R.id.viewpager, R.id.viewpager2}</b>)
 * void onPageScrolledOnMultipleViewPager(ViewPager v, int position) {
 * 	// Something Here
 * }
 *
 * &#064;PageScrolled(<b>R.id.viewpager</b>)
 * void onPageScrolledNoParam() {
 * 	// Something Here
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see PageScrollStateChanged
 * @see PageSelected
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface PageScrolled {

	/**
	 * The R.id.* fields which refer to the ViewPager.
	 *
	 * @return the ids of the ViewPager.
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource names as a strings which refer to the ViewPagers.
	 *
	 * @return the resource names of the ViewPagers.
	 */
	String[] resName() default "";

}
