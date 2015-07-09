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
 * Should be used on {@link android.app.Application} classes to enable usage of
 * AndroidAnnotations.
 * </p>
 * <p>
 * Your code related to injected beans should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <p>
 * If the class is abstract, the enhanced application will not be generated.
 * Otherwise, it will be generated as a final class. You can use
 * AndroidAnnotations to create Abstract classes that handle common code.
 * </p>
 * <p>
 * Most annotations are supported in {@link EApplication} classes, except the
 * ones related to views and extras.
 * </p>
 * <p>
 * The enhanced application can also be injected in any enhanced class by using
 * {@link App} annotation.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EApplication
 * public class MyApplication extends Application {
 * 
 * 	&#064;Bean
 * 	MyBean myBean;
 * 
 * 	&#064;AfterInject
 * 	void init() {
 * 		myBean.doSomeStuff();
 * 	}
 * }
 * 
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;App
 * 	MyApplication myApp;
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterInject
 * @see App
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EApplication {
}
