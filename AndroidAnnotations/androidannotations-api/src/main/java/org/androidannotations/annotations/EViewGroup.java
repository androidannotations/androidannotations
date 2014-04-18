/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
 * Should be used on {@link android.view.View} classes to enable usage of
 * AndroidAnnotations.
 * <p/>
 * Your code related to injected beans should go in an {@link AfterInject}
 * annotated method.
 * <p/>
 * Any view related code should happen in an {@link AfterViews} annotated
 * method.
 * <p/>
 * If the class is abstract, the enhanced view will not be generated. Otherwise,
 * it will be generated as a final class. You can use AndroidAnnotations to
 * create Abstract classes that handle common code.
 * <p/>
 * The annotation value should be one of R.layout.* fields. If not set, no
 * content view will be set, and you should inflate the layout yourself by
 * calling View.inflate() method</b>
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EViewGroup(R.layout.component)
 * public class CustomFrameLayout extends FrameLayout {
 * 
 * 	&#064;ViewById
 * 	TextView titleView;
 * 
 * 	&#064;AfterViews
 * 	void initViews() {
 * 		titleView.setText(&quot;test&quot;);
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterInject
 * @see AfterViews
 * @see ViewById
 * @see View
 * @see <a
 *      href="http://developer.android.com/guide/topics/ui/custom-components.html"
 *      >How to build a custom component.</a>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EViewGroup {
	int value() default ResId.DEFAULT_VALUE;

	String resName() default "";
}
