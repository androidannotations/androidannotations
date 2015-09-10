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
 * Use it on a {@link java.util.List} of {@link android.view.View} or
 * {@link android.view.View} subtype fields in a view related (ie
 * {@link org.androidannotations.annotations.EActivity},
 * {@link org.androidannotations.annotations.EFragment},
 * {@link org.androidannotations.annotations.EViewGroup}, ...) annotated class.
 * </p>
 * <p>
 * The annotation value should be an array of R.id.* fields.
 * </p>
 * <p>
 * Your code related to injected views should go in an
 * {@link org.androidannotations.annotations.AfterViews} annotated method.
 * </p>
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * 	// Injects R.id.edit1 and R.id.edit2 into the List.
 * 	&#064;ViewsById({ R.id.edit1, R.id.edit2 })
 * 	List&lt;EditText&gt; myEditTexts;
 * 
 * 	&#064;ViewsById({ R.id.myTextView1, R.id.myOtherTextView })
 * 	List&lt;TextView&gt; textViews;
 * 
 * 	&#064;AfterViews
 * 	void updateTextWithDate() {
 * 		for (TextView textView : textViews) {
 * 			textView.setText(&quot;Date: &quot; + new Date());
 * 		}
 * 	}
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see org.androidannotations.annotations.AfterViews
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ViewsById {

	/**
	 * The array of R.id.* fields which refer to the injected Views.
	 * 
	 * @return the ids of the Views
	 */
	int[] value() default ResId.DEFAULT_VALUE;

	/**
	 * The array resource names which refer to the injected Views.
	 * 
	 * @return the resource names of the Views
	 */
	String[] resName() default "";
}
