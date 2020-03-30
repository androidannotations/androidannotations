/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
 * Use on methods in {@link Rest} annotated classes to add multiple headers to a
 * given method.
 * 
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 * 
 * 	&#064;Headers({ &#064;Header(name = &quot;keep-alive&quot;, value = &quot;300&quot;), &#064;Header(name = &quot;cache-control&quot;, value = &quot;64000&quot;) })
 * 	&#064;Post(&quot;/test&quot;)
 * 	void testRoute();
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Header
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Headers {

	/**
	 * The set of added HTTP headers.
	 *
	 * @return the headers
	 */
	Header[] value();
}
