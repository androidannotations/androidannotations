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
 * Use it on {@link android.view.View} or {@link android.view.View} subtype
 * fields in a view related (ie {@link EActivity}, {@link EFragment},
 * {@link EViewGroup}, ...) annotated class.
 * </p>
 * <p>
 * The annotation value should be one of R.id.* fields. If not set, the field
 * name will be used as the R.id.* field name.
 * </p>
 * <p>
 * Your code related to injected views should go in an {@link AfterViews}
 * annotated method.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * 	// Injects R.id.myEditText
 * 	&#064;ViewById
 * 	EditText myEditText;
 * 
 * 	&#064;ViewById(R.id.myTextView)
 * 	TextView textView;
 * 
 * 	&#064;AfterViews
 * 	void updateTextWithDate() {
 * 		myEditText.setText(&quot;Date: &quot; + new Date());
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterViews
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ViewById {

	/**
	 * The R.id.* field which refers to the injected View.
	 * 
	 * @return the id of the View
	 */
	int value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource name which refers to the injected View.
	 * 
	 * @return the resource name of the View
	 */
	String resName() default "";
}
