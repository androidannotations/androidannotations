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
 * Use on {@link Get}, {@link Post}, … annotated methods to negotiate the
 * response format expected, and so the converter to use.
 * </p>
 * <p>
 * The annotation {@link #value()} is mandatory and define the <a
 * href="https://en.wikipedia.org/wiki/Internet_media_type">media type</a> to
 * accept. We provide a {@link org.androidannotations.api.rest.MediaType
 * MediaType} class to help you.
 * </p>
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(rootUrl = &quot;http://myserver&quot;, converters = { MappingJacksonHttpMessageConverter.class, SimpleXmlHttpMessageConverter.class })
 * public interface MyRestClient {
 * 
 * 	&#064;Get(&quot;/events/{id}&quot;)
 * 	&#064;Accept(<b>MediaType.APPLICATION_JSON</b>)
 * 	Event getEvent(long id);
 * 
 * 	&#064;Post(&quot;/entity&quot;)
 * 	&#064;Accept(<b>MediaType.APPLICATION_XML</b>)
 * 	Event addEvent(Event event);
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
public @interface Accept {

	/**
	 * The accepted media type.
	 * 
	 * @see org.androidannotations.api.rest.MediaType
	 * 
	 * @return the media type
	 */
	String value();
}
