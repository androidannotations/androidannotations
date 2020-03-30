/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
 * by <code>OnPreferenceClickListener#onPreferenceClick(Preference)</code> when
 * the <code>Preference</code> has been clicked by the user.
 * </p>
 * <p>
 * This annotation only can be used inside
 * {@link org.androidannotations.annotations.EActivity EActivity} or
 * {@link org.androidannotations.annotations.EFragment EFragment} annotated
 * class, which is a subclass of {@link android.preference.PreferenceActivity
 * PreferenceActivity} or <code>PreferenceFragment(Compat)</code>, respectively.
 * </p>
 * <p>
 * The annotation value should be one or several of R.string.* fields. If not
 * set, the method name will be used as the R.string.* field name.
 * </p>
 * <p>
 * The method MAY have one parameter:
 * </p>
 * <ul>
 * <li>A {@link android.preference.Preference Preference} (or a subclass) or
 * <code>android.support.v7.preference.Preference</code> (or a subclass)
 * parameter to know which preference has been clicked</li>
 * </ul>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;PreferenceClick(<b>R.string.myPref</b>)
 * void clickOnMyPref() {
 * 	// Something Here
 * }
 * 
 * &#064;PreferenceClick
 * void <b>myPref</b>PreferenceClicked(Preference preference) {
 * 	// Something Here
 * }
 * 
 * &#064;PreferenceClick
 * void <b>myPref</b>PreferenceClicked(ListPreference preference) {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see PreferenceChange
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface PreferenceClick {

	/**
	 * The R.string.* fields which refer to the Preferences.
	 * 
	 * @return the keys of the Preferences
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource names which refer to the Preferences.
	 * 
	 * @return the keys of the Preferences
	 */
	String[] resName() default "";
}
