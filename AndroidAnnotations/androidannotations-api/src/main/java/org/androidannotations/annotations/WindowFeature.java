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
 * Should be used on {@link EActivity} classes to set custom window features.
 * </p>
 * <p>
 * The annotation value should be one or several of {@link android.view.Window}
 * constants.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;WindowFeature({ Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS })
 * &#064;EActivity
 * public class MyActivity extends Activity {
 * 
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see EActivity
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface WindowFeature {

	/**
	 * An array of integers which are <code>Window.FEATURE_*</code> fields.
	 * 
	 * @return the Window feature constants
	 */
	int[] value();
}
