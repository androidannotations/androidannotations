/**
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
 * Use it on Fragment fields in an {@link EBean} annotated classes to inject the
 * Fragment which originally injected the bean instance.
 * </p>
 * <p>
 * This field may not be injected at runtime if the fragment used to create the
 * bean is not of the appropriate type, or if the bean is injected in an
 * activity.
 * </p>
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;EBean
 * public class MyClass {
 * 
 * 	&#064;RootFragment
 * 	Fragment fragment;
 * 
 * 	// Only injected if the root fragment implements a given interface
 * 	&#064;RootFragment
 * 	MyInterface fragment;
 * }
 * </pre>
 *
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface RootFragment {
}
