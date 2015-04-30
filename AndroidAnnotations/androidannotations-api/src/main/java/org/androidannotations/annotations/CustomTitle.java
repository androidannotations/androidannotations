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
 * Use it on {@link EActivity} annotated classes to set a custom title layout.
 * </p>
 * <p>
 * The annotation value is mandatory and should be one of R.layout.* fields.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;CustomTitle(R.layout.activityTitleLayout)
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * }
 * </pre>
 * 
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface CustomTitle {

	/**
	 * R.layout.* field which refers the the title layout.
	 * 
	 * @return the id of the layout
	 */
	int value();
}
