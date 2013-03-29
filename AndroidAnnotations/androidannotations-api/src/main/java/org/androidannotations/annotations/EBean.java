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
 * Should be used on custom classes to enable usage of AndroidAnnotations
 * 
 * Any view related code should happen in an {@link AfterViews} annotated
 * method.<br>
 * <br>
 * 
 * Most annotations are supported in {@link EBean} classes
 * 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EBean {
	
	public enum Scope {

		/**
		 * A new instance of the bean is created each time it is needed
		 */
		Default, //

		/**
		 * A new instance of the bean is created the first time it is needed, it is
		 * then retained and the same instance is always returned.
		 */
		Singleton, //
	}

	Scope scope() default Scope.Default;
	
}
