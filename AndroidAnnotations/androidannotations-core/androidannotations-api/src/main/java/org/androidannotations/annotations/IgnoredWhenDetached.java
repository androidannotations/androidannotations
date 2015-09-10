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
 * When used standalone in an {@link EFragment} or in conjunction with the
 * {@link UiThread} or {@link Background} annotations, the annotated method will
 * be wrapped in an 'if attached' block such that no code will be executed if
 * the {@link EFragment} is no longer bound to its parent activity.
 * </p>
 * <p>
 * Should be used on method that must meet the following criteria
 * </p>
 * <p>
 * 1) Can only be used in conjunction with classes annotated with
 * {@link EFragment}
 * </p>
 * <p>
 * 2) The annotated method MUST return void and MAY contain parameters.
 * </p>
 *
 * <blockquote> <b>Example</b> :
 *
 * <pre>
 * &#064;EFragment
 * public class LoaderFragment extends Fragment {
 * 
 * ...
 * 
 * 	&#064;UiThread
 * 	&#064;IgnoredWhenDetached
 * 	void killActivity() {
 * 		getActivity().finish();
 * 	}
 * 
 * 
 * 	&#064;IgnoredWhenDetached
 * 	void updateTitle(String title) {
 * 		getActivity().setTitle(title);
 * 	}
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see org.androidannotations.annotations.EFragment
 * @see org.androidannotations.annotations.UiThread
 * @see org.androidannotations.annotations.Background
 * @see android.os.Handler
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface IgnoredWhenDetached {

}