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
 * {@link android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference.Preference, Object)
 * OnPreferenceChangeListener#onPreferenceChange} when the value of a
 * {@link android.preference.Preference Preference} has been changed by the user
 * and is about to be set and/or persisted.
 * </p>
 * <p>
 * The annotation value should be one or several R.string.* fields that refers
 * to {@link android.preference.Preference Preference} or subclasses of
 * {@link android.preference.Preference Preference}. If not set, the method name
 * will be used as the R.string.* field name.
 * </p>
 * <p>
 * The method MAY have multiple parameter:
 * </p>
 * <ul>
 * <li>A {@link android.preference.Preference Preference} (or a sublcass)
 * parameter to know which preference was targeted by this event</li>
 * <li>An {@link Object}, {@link String}, {@link java.util.Set Set of strings}
 * and also a {@link Boolean}, {@link Float}, {@link Integer}, {@link Long} or
 * their corresponding primitive types to obtain the new value of the
 * {@link android.preference.Preference Preference}. Please note with number
 * types, we assume that the <code>newValue</code> parameter coming from the
 * {@link android.preference.Preference Preference} is a {@link String}, so we
 * parse it to a number object (Android {@link android.preference.Preference
 * Preference} classes use {@link String}s instead of number objects).</li>
 * </ul>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;PreferenceChange(<b>R.string.myPref</b>)
 * void checkedChangedOnMyButton(boolean newValue, Preference preference) {
 * 	// Something Here
 * }
 * 
 * &#064;PreferenceChange
 * void <b>myPref</b>PreferenceChanged(Preference preference) {
 * 	// Something Here
 * }
 * 
 * &#064;PreferenceChange
 * void <b>myPref</b>PreferenceChanged(ListPreference preference) {
 * 	// Something Here
 * }
 * 
 * &#064;PreferenceChange(<b>{R.string.myPref1, R.string.myPref2}</b>)
 * void preferenceChangeOnMultiplePrefs(Preference preference, String newValue) {
 * 	// Something Here
 * }
 * 
 * &#064;PreferenceChange(<b>R.string.myPref</b>)
 * void preferenceChangeOnMyPref() {
 * 	// Something Here
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see PreferenceClick
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface PreferenceChange {

	int[] value() default ResId.DEFAULT_VALUE;

	String[] resName() default "";
}
