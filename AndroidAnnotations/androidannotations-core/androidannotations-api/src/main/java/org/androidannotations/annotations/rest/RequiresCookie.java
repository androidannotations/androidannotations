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
 * Use on {@link Get}, {@link Post}, … annotated methods to inject a cookie in
 * the request.
 * </p>
 * <p>
 * The annotation {@link #value()} is mandatory and define the cookie's name you
 * want to inject.
 * </p>
 * <p>
 * To set a cookie's value you MUST either let your RestClient interface extends
 * of {@link org.androidannotations.api.rest.RestClientHeaders
 * RestClientHeaders} or add the following method to your interface :
 * <code>void setCookie(String name, String value)</code>.
 * </p>
 * <p>
 * You can also add the getter version to read a cookie value :
 * <code>void getCookie(String name)</code>.
 * </p>
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(rootUrl = &quot;http://myserver&quot;, converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 * 
 * 	&#064;Get("/events/{id}")
 * 	&#064;RequiresCookie("session")
 * 	Event getEvent(long id);
 *   
 * 	void setCookie(String name, String value);
 * 	String getCookie(String name);
 * }
 * 
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;RestService MyRestClient;
 * 
 * 	&#064;AfterInject
 * 	public void init() {
 * 		myRestClient.setCookie("session", "my session uid");
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Rest
 * @see Get
 * @see Post
 * @see Put
 * @see Delete
 * @see Head
 * @see Options
 * @see SetsCookie
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RequiresCookie {

	/**
	 * The names of the cookies.
	 * 
	 * @return the cookie names
	 */
	String[] value();
}
