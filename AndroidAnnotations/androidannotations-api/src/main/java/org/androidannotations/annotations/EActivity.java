/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
 * Should be used on Activity classes to enable usage of AndroidAnnotations
 * 
 * Any view related code should happen in an {@link AfterViews} annotated
 * method.
 * 
 * The annotation value should be one of R.layout.* fields. If not set, no
 * content view will be set, and you should call the setContentView() method
 * yourself, in <b>onCreate()</b>
 * 
 * If the activity is abstract, the generated activity will not be final.
 * Otherwise, it will be final. You can use AndroidAnnotations to create
 * Abstract classes that handle common code, but we want you to make a clear
 * decision on that subject.
 * 
 * The main reason is that the generated code should not be considered a public
 * API, and may change between releases. So if you extend some generated
 * activity, you may have to adapt your code when you upgrade
 * AndroidAnnotations.
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EActivity {
	int value() default ResId.DEFAULT_VALUE;

	String resName() default "";
}
