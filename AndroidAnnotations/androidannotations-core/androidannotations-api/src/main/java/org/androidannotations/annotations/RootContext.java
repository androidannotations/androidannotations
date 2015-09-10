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
 * Use it on {@link android.content.Context} fields in an {@link EBean}
 * annotated classes to inject context of the parent class.
 * </p>
 * <p>
 * This field may not be injected at runtime if the context used to create the
 * bean is not of the appropriate type. For example, if you create a new
 * instance of the bean using a Service context, and you use {@link RootContext}
 * on a field that extends Activity, this field will be null at runtime.
 * </p>
 * <blockquote>
 *
 * Example :
 *
 * <pre>
 * &#064;EBean
 * public class MyClass {
 * 
 * 	&#064;RootContext
 * 	Context context;
 * 
 * 	// Only injected if the root context is an activity
 * 	&#064;RootContext
 * 	Activity activity;
 * 
 * 	// Only injected if the root context is a service
 * 	&#064;RootContext
 * 	Service service;
 * 
 * 	// Only injected if the root context is an instance of MyActivity
 * 	&#064;RootContext
 * 	MyActivity myActivity;
 * }
 * </pre>
 *
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface RootContext {
}
