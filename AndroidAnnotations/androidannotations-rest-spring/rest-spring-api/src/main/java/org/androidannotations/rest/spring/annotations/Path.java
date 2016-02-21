/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.rest.spring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to mark a method parameter to be an url variable.
 * This annotation is optional, because method parameters which are not
 * annotated with any annotation or not correspond to the request entity,
 * implicitly interpreted as url variables. However with this annotation you can
 * explicitly mark them. Also, with the annotation value, you can add another
 * name to the url variable. If an url variable method parameter does not have
 * the {@link Path} annotation, or the annotation value is not specified, the
 * method parameter name will be used as the url variable name. The url in the
 * {@link Get} etc. annotation must contain an url variable, which the
 * corresponding method variable will be substituted into.
 *
 * <blockquote>
 *
 * <b>Example :</b>
 *
 * <pre>
 * &#064;Rest(converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 *
 * 	&#064;Get(&quot;/event/<b>{id}</b>&quot;)
 * 	Event getEvent(String <b>id</b>);
 *
 * 	&#064;Get(&quot;/event/<b>{id}</b>&quot;)
 * 	Event getEventWithPathAnnotation(&#064;Path String <b>id</b>);
 *
 * 	&#064;Get(&quot;/event/<b>{id}</b>&quot;)
 * 	Event getEventWithPathAnnotationValue(&#064;Path(&quot;<b>id</b>&quot;) String identifier);
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @see Rest
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PARAMETER)
public @interface Path {

	/**
	 * Name of the url variable.
	 *
	 * @return the url variable name
	 */
	String value() default "";
}
