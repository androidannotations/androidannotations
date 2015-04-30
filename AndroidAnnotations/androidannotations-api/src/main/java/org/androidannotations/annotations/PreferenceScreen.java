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
 * Should be used on {@link org.androidannotations.annotations.EActivity
 * EActivity} or {@link org.androidannotations.annotations.EFragment EFragment}
 * classes which are subclass of {@link android.preference.PreferenceActivity
 * PreferenceActivity} or <code>PreferenceFragment</code>, to inject the
 * preference screen from resource.
 * </p>
 * <p>
 * The annotation value should be one of R.xml.* fields.
 * </p>
 * 
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;PreferenceScreen(R.xml.settings)
 * &#064;EActivity
 * public class SettingsActivity extends PreferenceActivity {
 * 
 * 	&#064;PreferenceByKey(R.string.myPref1)
 * 	Preference myPreference1;
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
 * @see PreferenceHeaders
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface PreferenceScreen {

	int value() default ResId.DEFAULT_VALUE;

	String resName() default "";
}
