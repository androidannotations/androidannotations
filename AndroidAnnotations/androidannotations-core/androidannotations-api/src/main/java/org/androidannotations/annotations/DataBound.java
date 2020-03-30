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
 * Can be used on enhanced Activities, Fragments and ViewGroups to mark them to
 * use the Data Binding library to inflate the layout.
 * </p>
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;DataBound
 * &#064;EActivity(R.layout.my_activity)
 * public class MyActivity extends Activity {
 *
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see BindingObject
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface DataBound {
}
