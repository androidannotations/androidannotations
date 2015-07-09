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
 * Use on {@link Get}, {@link Post}, … annotated methods to inject a header in
 * the request.
 * </p>
 * <p>
 * The annotation {@link #value()} is mandatory and define the header's name you
 * want to inject.
 * </p>
 * <p>
 * To set a header's value you MUST either let your RestClient interface extends
 * of {@link org.androidannotations.api.rest.RestClientHeaders
 * RestClientHeaders} or add the following method to your interface :
 * <code>void setHeader(String name, String value)</code>.
 * </p>
 * <p>
 * You can also add the getter version to read a header value :
 * <code>void getHeader(String name)</code>.
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
 * 	&#064;RequiresHeader(&quot;myHeader&quot;)
 * 	Event getEvent(long id);
 * 
 * 	void setHeader(String name, String value);
 * 
 * 	String getHeader(String name);
 * }
 * 
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;RestService MyRestClient;
 * 
 * 	&#064;AfterInject
 * 	public void init() {
 * 		myRestClient.setHeader("myHeader", "myValue");
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
public @interface RequiresHeader {

	/**
	 * The names of the headers.
	 * 
	 * @return the header names
	 */
	String[] value();
}
