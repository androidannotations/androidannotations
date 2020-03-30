/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
 * Use on activity fields to save and restore their values when the system calls
 * <code>onSaveInstanceState(Bundle)</code> and <code>onCreate(Bundle)</code>.
 * </p>
 * <p>
 * Use on any native, {@link android.os.Parcelable Parcelable} or
 * {@link java.io.Serializable Serializable} field in an {@link EActivity}
 * annotated class to bind it with Android's arguments. If
 * <a href="http://parceler.org">Parceler</a> is on the classpath, extras
 * annotated with &#064;Parcel, or collections supported by Parceler will be
 * automatically marshaled using a {@link android.os.Parcelable Parcelable}
 * through the Parcels utility class.
 * </p>
 *
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EActivity
 * public class MyActivity extends Activity {
 * 
 * 	&#064;InstanceState
 * 	int someId;
 * 
 * 	&#064;InstanceState
 * 	MySerializableBean bean;
 * }
 * </pre>
 * 
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface InstanceState {
}
