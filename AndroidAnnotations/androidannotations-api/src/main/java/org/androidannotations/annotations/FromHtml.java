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
 * Use on a {@link android.widget.TextView TextView} field or a
 * {@link android.widget.TextView TextView} subclass field annotated with
 * {@link ViewById} to inject text as HTML.
 * </p>
 * <p>
 * The annotation value should be a R.string.* field that refers to string
 * resource. If not set, the method name will be used as the R.string.* field
 * name.
 * </p>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * 	&#064;ViewById(R.id.my_text_view)
 * 	&#064;FromHtml(R.string.hello_html)
 * 	TextView textView;
 * 
 * 	&#064;ViewById
 * 	&#064;FromHtml
 * 	TextView someView;
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see ViewById
 * @see org.androidannotations.annotations.res.HtmlRes
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface FromHtml {

	/**
	 * The R.string.* field which refers to the html string resource.
	 * 
	 * @return the id of the resource
	 */
	int value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource name which refers to the html string resource.
	 * 
	 * @return the resource name of the resource
	 */
	String resName() default "";
}
