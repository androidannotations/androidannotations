/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
import java.util.Set;

/**
 * Use on methods in {@link Rest} annotated class to add a new rest service of
 * type OPTIONS.
 * <p/>
 * This annotation as the same constraints as {@link Get} but it MUST return a
 * {@link Set} of {@link org.springframework.http.HttpMethod}
 * <p/>
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(rootUrl = &quot;http://myserver&quot;, converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 * 
 * 	&#064;Options(&quot;/events&quot;)
 * 	Set&lt;HttpMethod&gt; getEventOptions();
 * 
 * 	&#064;Options(&quot;/events/{year}/{location}&quot;)
 * 	Set&lt;HttpMethod&gt; getEventOptions(String location, int year);
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
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Options {
	String value();
}
