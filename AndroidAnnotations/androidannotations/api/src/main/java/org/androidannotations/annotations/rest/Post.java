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
package org.androidannotations.annotations.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Use on methods in {@link Rest} annotated class to add a new rest service of
 * type POST.
 * </p>
 * <p>
 * This annotation as the same constraints as {@link Get}.
 * </p>
 * <p>
 * If an extra parameter is present (ie the only one not mapped with URI
 * placeholders) it will be send in the request body using given converter.
 * </p>
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(rootUrl = &quot;http://myserver&quot;, converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 * 
 * 	&#064;Post(&quot;/events/new/random&quot;)
 * 	Event newEvent();
 * 
 * 	&#064;Post(&quot;/events/new/<b>{id}</b>&quot;)
 * 	void newEvent(Event <i>event</i>, int <b>id</b>);
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Rest
 * @see Get
 * @see Put
 * @see Delete
 * @see Head
 * @see Options
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Post {

	/**
	 * The URI or the full URL of the web service.
	 * 
	 * @return the address of the web service
	 */
	String value();
}
