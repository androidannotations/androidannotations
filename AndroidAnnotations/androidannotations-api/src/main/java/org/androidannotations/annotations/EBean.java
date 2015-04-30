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
 * Should be used on custom classes to enable usage of AndroidAnnotations.
 * </p>
 * <p>
 * This class MUST have either a default constructor (ie without parameters) or
 * a constructor with only a parameter of type {@link android.content.Context}.
 * </p>
 * <p>
 * Your code related to injected beans should go in an {@link AfterInject}
 * annotated method.
 * </p>
 * <p>
 * If the class is abstract, the enhanced bean will not be generated. Otherwise,
 * it will be generated as a final class. You can use AndroidAnnotations to
 * create Abstract classes that handle common code.
 * </p>
 * <p>
 * Most annotations are supported in {@link EBean} classes, except the ones
 * related to extras. Views related annotations will only work if the bean was
 * injected in an activity with a layout containing the views you're dealing
 * with. If your bean needs a {@link android.content.Context} you can inject on
 * by using an {@link RootContext} annotated field.
 * </p>
 * <p>
 * Beans have two possible scopes : default or singleton. Default scope should
 * be preferred but in some case it may be useful to use a singleton scope
 * (mainly if you want to keep some runtime state in your bean).
 * </p>
 * <p>
 * The enhanced bean can also be injected in any enhanced class by using
 * {@link Bean} annotation.
 * </p>
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;RootContext
 * 	Context context;
 * 	&#064;Bean
 * 	MySingletonBean mySingletonBean;
 * 
 * 	&#064;AfterInject
 * 	void init() {
 * 		mySingletonBean.doSomeStuff(context);
 * 	}
 * }
 * 
 * &#064;EBean(scope = Scope.Singleton)
 * public class MySingletonBean {
 * 
 * 	public void doSomeStuff(Context context) {
 * 		// ...
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see AfterInject
 * @see RootContext
 * @see Bean
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EBean {

	/**
	 * The scope of an enhanced bean.
	 */
	public enum Scope {

		/**
		 * A new instance of the bean is created each time it is needed.
		 */
		Default, //

		/**
		 * A new instance of the bean is created the first time it is needed, it
		 * is then retained and the same instance is always returned.
		 */
		Singleton, //
	}

	/**
	 * The scope of the enhanced bean.
	 * 
	 * @return the scope of the bean.
	 */
	Scope scope() default Scope.Default;

}
