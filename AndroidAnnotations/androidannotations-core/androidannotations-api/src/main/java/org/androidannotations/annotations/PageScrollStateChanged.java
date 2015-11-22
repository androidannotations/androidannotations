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
 * <code>android.support.v4.view.ViewPager.OnPageChangeListener.onPageScrollStateChanged</code>
 * when the scroll state changes.
 * </p>
 * <p>
 * The annotation value should be one or several R.id.* fields that refers to
 * ViewPager or subclasses of ViewPager. If not set, the method name will be
 * used as the R.id.* field name.
 * </p>
 * <p>
 * The method MAY have multiple parameter :
 * </p>
 * <ul>
 * <li>A ViewPager parameter to know which view has targeted this event</li>
 * <li>An int parameter is to get new scroll state.</li>
 * </ul>
 *
 * <blockquote>
 *
 * Examples :
 *
 * <pre>
 * &#064;PageScrollStateChanged(<b>R.id.viewpager</b>)
 * void onPageScrollStateChanged(ViewPager view, int state) {
 * 	// Something Here
 * }
 *
 * &#064;PageScrollStateChanged
 * void viewPager(ViewPager view) {
 * 	// Something Here
 * }
 *
 * &#064;PageScrollStateChanged(<b>{R.id.viewpager, R.id.viewpager2}</b>)
 * void onPageScrollStateChangedOnMultipleViewPager(ViewPager v, int anything) {
 * 	// Something Here
 * }
 *
 * &#064;PageScrollStateChanged(<b>R.id.viewpager</b>)
 * void onPageScrollStateChangedNoParam() {
 * 	// Something Here
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see PageScrolled
 * @see PageSelected
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface PageScrollStateChanged {

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
