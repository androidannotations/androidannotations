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
 * Use on {@link Get}, {@link Post}, … annotated methods to retrieve and store
 * cookies from the HTTP response.
 * </p>
 * <p>
 * The annotation {@link #value()} is mandatory and define a list of cookie's
 * names you want to keep.
 * </p>
 * <p>
 * Each stored cookies can be re-used with {@link RequiresCookie} and
 * {@link RequiresCookieInUrl} annotations. The <code>getCookie</code> method
 * will also be able to read these cookies.
 * </p>
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(rootUrl = &quot;http://myserver&quot;, converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 * 
 * 	&#064;Get(&quot;/events/{id}&quot;)
 * 	&#064;SetsCookie((<b>&quot;token&quot;(</b>)
 * 	Event getEvent(long id);
 * 
 * 	&#064;Put(&quot;/events/update/&quot;)
 * 	&#064;RequiresCookie(<b>&quot;token&quot;</b>)
 * 	void updateEvent(Event event);
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Rest
 * @see RequiresCookie
 * @see SetsCookie
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SetsCookie {

	/**
	 * The names of the cookies to be kept.
	 * 
	 * @return the cookie names
	 */
	String[] value();
}
