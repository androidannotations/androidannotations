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
package org.androidannotations.rest.spring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to add a method body to the POST, PUT, or
 * PATCH request from a method parameter.
 *
 * <b>Example :</b>
 *
 * <pre>
 * &#064;Rest(rootUrl = &quot;http://myserver&quot;, converters = FormHttpMessageConverter.class)
 * public interface RestClient {
 *
 * 	&#064;Post(&quot;/events/{id}&quot;)
 * 	EventList addEvent(String id, <b>&#064;Body</b> Event event);
 * }
 * </pre>
 *
 * @see Rest
 * @see Post
 * @see Put
 * @see Patch
 * @see Field
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PARAMETER)
public @interface Body {
}
