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
 * Apply @{@link Rest} on an interface to create a RestService class that will
 * contain implementation of rest calls related to the methods you define in the
 * interface.
 * </p>
 * <p>
 * You should then inject your RestService class by using {@link RestService}
 * annotation in any enhanced classes.
 * </p>
 * <p>
 * <b>Note:</b> Implementation is based on <a href=
 * "http://docs.spring.io/spring-android/docs/current/reference/htmlsingle/"
 * >Spring Android Rest-template</a> library. So you <b>MUST</b> have the
 * library in your classpath and we highly recommend you to take some time to
 * read this document and understand how the library works.
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
 * 	Event getEvent(long id);
 * 
 * 	&#064;Post(&quot;/events/new&quot;)
 * 	void newEvent(Event event);
 * }
 * 
 * &#064;EBean
 * public class MyBean {
 * 
 * 	&#064;RestService
 * 	MyRestClient myRestClient;
 * 
 * 	public void getEvent(long id) {
 * 		return myRestClient.getEvent(id);
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see Rest
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface RestService {
}
