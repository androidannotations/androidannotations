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
 * type GET.
 * </p>
 * <p>
 * The annotation {@link #value()} is mandatory and define the URI or the full
 * URL of the web service. It MAY contain placeholders defined as follow :
 * <code>{name}</code>
 * </p>
 * <p>
 * The annotated method MAY have parameters as soon as each parameter names are
 * present as placeholders in the URI.
 * </p>
 * <p>
 * The annotated method CAN return <code>void</code>,
 * {@link org.springframework.http.ResponseEntity} or any concrete java classes.
 * Interfaces CAN'T be used as return type because converters have to know which
 * object to instantiate while returning result.
 * </p>
 * <p>
 * <b>Note:</b> Generics classes are also supported both for return type and
 * parameters.
 * </p>
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(rootUrl = &quot;http://myserver&quot;, converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 * 
 * 	&#064;Get(&quot;/events&quot;)
 * 	EventList getEvents();
 * 
 * 	&#064;Get(&quot;/events/<b>{max}</b>&quot;)
 * 	ResponseEntity&lt;EventList&gt; getEvents(int <b>max</b>);
 * 
 * 	&#064;Get(&quot;/events/<b>{max}</b>/<b>{filter}</b>&quot;)
 * 	ArrayList&lt;Event&gt; getEvents(int <b>max</b>, String <b>filter</b>);
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Rest
 * @see Post
 * @see Put
 * @see Delete
 * @see Head
 * @see Options
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Get {

	/**
	 * The URI or the full URL of the web service.
	 * 
	 * @return the address of the web service
	 */
	String value();
}
