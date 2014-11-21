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
 * Methods annotated with {@link AfterPreferences} will be called after
 * <code>addPreferenceFromResource</code> is called by the generated classs.
 * </p>
 * <p>
 * This occurs AFTER <code>addPreferencesFromResource</code> which is called at
 * the end of super.onCreate(). Any preference depending code should be done in
 * an {@link AfterPreferences} annotated method.
 * </p>
 * <p>
 * The method MUST have zero parameters.
 * </p>
 * <p>
 * There MAY be several methods annotated with {@link AfterPreferences} in the
 * same class.
 * </p>
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;EActivity
 * public class SettingsActivity extends PreferenceActivity {
 * 
 * 	&#064;PreferenceByKey(R.string.checkBoxPref)
 * 	CheckBoxPreference checkBoxPref;
 * 
 * 	&#064;AfterPreferences
 * 	void initPrefs() {
 * 		checkBoxPref.setChecked(false);
 * 	}
 * }
 * </pre>
 *
 * </blockquote>
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AfterPreferences {
}
