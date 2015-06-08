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
 * Should be used on android.app.Fragment or android.support.v4.app.Fragment
 * classes to enable usage of AndroidAnnotations.
 * </p>
 * <p>
 * Your code related to injected beans should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <p>
 * Any view related code should happen in an {@link AfterViews} annotated
 * method.
 * </p>
 * <p>
 * If the class is abstract, the enhanced activity will not be generated.
 * Otherwise, it will be generated as a final class. You can use
 * AndroidAnnotations to create Abstract classes that handle common code.
 * </p>
 * <p>
 * The annotation value should be one of R.layout.* fields. If not set, no
 * content view will be set, and you should call the
 * <code>inflater.inflate()</code> method yourself, in
 * <code>onCreateView()</code>.
 * </p>
 * <p>
 * We only set the layout if the <code>onCreateView()</code> method returns null
 * from the annotated class. If you want to set the layout regardless of that
 * return value, use the {@link #forceLayoutInjection()} annotation parameter.
 * </p>
 * <p>
 * The generated class will also contain a FragmentBuilder to build fragment
 * with a fluent API. Arguments can be passed by using {@link FragmentArg}
 * annotation on every native or serializable/parcelable field.
 * </p>
 * <p>
 * The enhanced fragment can also be retrieved (not injected in layout) in any
 * enhanced class by using {@link FragmentById} or {@link FragmentByTag}
 * annotations.
 * </p>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EFragment(R.layout.fragment)
 * public class MyFragment extends Fragment {
 * 
 * 	&#064;Bean
 * 	MyBean myBean;
 * 
 * 	&#064;ViewById
 * 	TextView myTextView;
 * 
 * 	&#064;FragmentArg
 * 	String myExtra;
 * 
 * 	&#064;AfterInject
 * 	void init() {
 * 		myBean.doSomeStuff();
 * 	}
 * 
 * 	&#064;AfterViews
 * 	void initViews() {
 * 		myTextView.setText(myExtra);
 * 	}
 * }
 * 
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * 	&#064;FragmentById
 * 	MyFragment myFragment;
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterInject
 * @see AfterViews
 * @see FragmentById
 * @see FragmentByTag
 * @see FragmentArg
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EFragment {

	/**
	 * The R.layout.* field which refer to the layout.
	 * 
	 * @return the id of the layout
	 */
	int value() default ResId.DEFAULT_VALUE;

	/**
	 * The resource name as a string which refer to the layout.
	 * 
	 * @return the resource name of the layout
	 */
	String resName() default "";

	/**
	 * We only set the layout if the <code>onCreateView()</code> method returns
	 * null from the annotated class. If you want to set the layout regardless
	 * of that return value, pass <b>true</b> to this annotation parameter.
	 * 
	 * @return <b>true</b>, if the layout must be set, <b>false</b> otherwise
	 */
	boolean forceLayoutInjection() default false;
}
