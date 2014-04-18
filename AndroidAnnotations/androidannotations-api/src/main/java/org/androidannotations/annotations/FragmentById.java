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
 * Use it on {@link android.app.Fragment} or
 * {@link android.support.v4.app.Fragment} fields in activity classes to
 * retrieve and inject a fragment.
 * <p/>
 * The annotation value should be one of R.id.* fields. If not set, the field
 * name will be used as the R.id.* field name.
 * <p/>
 * <b>Note:</b> This can only inject an existing fragment, not create them.
 * <p/>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &lt;LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent" >
 * 
 *     &lt;fragment
 *         android:id="@+id/myFragment"
 *         android:name="mypackage.MyFragment_"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent" />
 * &lt;/LinearLayout>
 * 
 * 
 * &#064;EActivity(R.layout.main)
 * public class MyActivity extends Activity {
 * 
 * // all injected fragment will be the same
 * 
 * 	&#064;FragmentById
 * 	public MyFragment myFragment;
 * 	
 * 	&#064;FragmentById(R.id.myFragment)
 * 	public MyFragment myFragment2;
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see EFragment
 * @see FragmentArg
 * @see FragmentByTag
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface FragmentById {
	int value() default ResId.DEFAULT_VALUE;

	String resName() default "";
}
