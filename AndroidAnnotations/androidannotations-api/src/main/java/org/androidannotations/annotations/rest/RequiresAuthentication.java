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
 * Use on {@link Get}, {@link Post}, … annotated methods to use authentication
 * on the request.
 * </p>
 * <p>
 * To set the current authentication object to use, you MUST either let your
 * RestClient interface extends of
 * {@link org.androidannotations.api.rest.RestClientHeaders RestClientHeaders}
 * or add the following method to your interface :
 * <code>void setAuthentication(org.springframework.http.HttpAuthentication auth)</code>
 * .
 * </p>
 * <p>
 * You can also add a specific method for <a
 * href="https://en.wikipedia.org/wiki/Basic_access_authentication">Basic
 * Authentication</a> :
 * <code>setHttpBasicAuth(String username, String password)</code> or <a
 * href="https://tools.ietf.org/html/rfc6750" >Bearer (OAuth) authentication</a>
 * : <code>setBearerAuth(String token)</code>.
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
 * 	&#064;RequiresAuthentication
 * 	Event getEvent(long id);
 * 
 * 	setHttpBasicAuth(String username, String password);
 * }
 * 
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;RestService MyRestClient;
 * 
 * 	&#064;AfterInject
 * 	public void init() {
 * 		myRestClient.setHttpBasicAuth("user", "password");
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
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RequiresAuthentication {

}
