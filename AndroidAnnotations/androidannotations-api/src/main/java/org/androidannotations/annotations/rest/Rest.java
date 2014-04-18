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

import org.androidannotations.api.rest.RestClientHeaders;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;

/**
 * Apply @{@link Rest} on an interface to create a RestService class that will
 * contain implementation of rest calls related to the methods you define in the
 * interface.
 * <p/>
 * You should then inject your RestService class by using {@link RestService}
 * annotation in any enhanced classes.
 * <p/>
 * <b>Note:</b> Implementation is based on <a href=
 * "http://docs.spring.io/spring-android/docs/current/reference/htmlsingle/"
 * >Spring Android Rest-template</a> library. So you <b>MUST</b> have the
 * library in your classpath and we highly recommend you to take some time to
 * read this document and understand how the library works.
 * <p/>
 * 
 * 
 * <h1>Converters</h1>
 * Every {@link Rest} annotated interface MUST define at least one
 * {@link #converters()} to tell the library how to convert received data into
 * Java objects.
 * <p/>
 * {@link #converters()} value MAY contain one or several
 * {@link org.springframework.http.converter.HttpMessageConverter} sub-classes
 * <p/>
 * <blockquote>
 * 
 * <b>Example :</b> The following RestClient will use <a
 * href="http://jackson.codehaus.org/">Jackson</a> to deserialize received data
 * as Java objects.
 * 
 * <pre>
 * &#064;Rest(<b>converters</b> = MappingJackson2HttpMessageConverter.class)
 * public interface RestClient {
 * 
 * 	&#064;Get(&quot;http://myserver/events&quot;)
 * 	EventList getEvents();
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * <h1>Root url</h1>
 * If you don't wan't to repeat the root URL in each method, you MAY like the
 * {@link #rootUrl()} field. It let you define a common root URL which will be
 * prefixed on every method of your RestClient.
 * <p/>
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(<b>rootUrl</b> = &quot;http://myserver&quot;, converters = MappingJackson2HttpMessageConverter.class)
 * public interface RestClient {
 * 
 * 	&#064;Get(&quot;/events&quot;)
 * 	EventList getEvents();
 * 
 * 	&#064;Get(&quot;/lastevent&quot;)
 * 	Event getLastEvent();
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * <h1>Interceptors</h1>
 * Sometimes you may want to do extra processing right before or after requests.
 * {@link #interceptors()} field let you define one or several
 * {@link org.springframework.http.client.ClientHttpRequestInterceptor}.
 * <p/>
 * An interceptor allow the developer to customize the execution flow of
 * requests. It may be useful to handle custom authentication, automatically log
 * each requests, and so on.
 * <p/>
 * <blockquote>
 * 
 * <b>Example :</b>
 * 
 * <pre>
 * &#064;Rest(converters = MappingJacksonHttpMessageConverter.class, interceptors = HttpBasicAuthenticatorInterceptor.class)
 * public interface MyRestClient {
 * 
 * 	&#064;Get(&quot;/events&quot;)
 * 	EventList getEvents();
 * }
 * 
 * public class HttpBasicAuthenticatorInterceptor implements ClientHttpRequestInterceptor {
 * 
 * 	&#064;Override
 * 	public ClientHttpResponse intercept(HttpRequest request, byte[] data, ClientHttpRequestExecution execution) throws IOException {
 * 		// do something before sending request
 * 		return execution.execute(request, data);
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * 
 * <h1>Magic methods</h1>
 * AA will automatically detect and implement some methods in {@link Rest}
 * annotated interface. These methods will let you dynamically customize the
 * RestClient.
 * 
 * <h2>RootUrl</h2>
 * We seen earlier that root url can be set via {@link #rootUrl()} annotation
 * field, but it only takes a constant. If you want to dynamically inject or
 * retrieve the root url, you can add the following code :
 * <p/>
 * <blockquote>
 * 
 * <pre>
 * &#064;Rest(converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 * 
 * 	void setRootUrl(String rootUrl);
 * 
 * 	String getRootUrl();
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * <h2>RestTemplate</h2>
 * If you want to configure the injected RestTemplate used internally, AA will
 * also detect getter and setter for this object.
 * <p/>
 * <blockquote>
 * 
 * <pre>
 * &#064;Rest(converters = MappingJacksonHttpMessageConverter.class)
 * public interface MyRestClient {
 * 
 * 	RestTemplate getRestTemplate();
 * 
 * 	void setRestTemplate(RestTemplate restTemplate);
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * <h2>Bundle interfaces</h2>
 * Since 3.0, we're also providing some bundle interface your RestClient can
 * extends of. Each of them provide handled methods subset and let you clean
 * your code by using extends composition instead of writing methods.
 * <p/>
 * Available bundle interfaces :
 * <ul>
 * <li><b>RestClientRootUrl</b>: provide <code>getRootUrl()</code> and
 * <code>setRootUrl()</code></li>
 * <li><b>RestClientSupport</b>: provide <code>getRestTemplate()</code> and
 * <code>setRestTemplate()</code></li>
 * <li><b>RestClientHeaders</b>: provide <code>getHeader()</code>,
 * <code>setHeader()</code>, <code>getCookie()</code>, <code>setCookie()</code>,
 * <code>setAuthentication()</code> and <code>setHttpBasicAuth()</code></li>
 * </ul>
 * 
 * 
 * @see RestService
 * @see RestClientSupport
 * @see RestClientRootUrl
 * @see RestClientHeaders
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Rest {
	String rootUrl() default "";

	Class<?>[] converters();

	Class<?>[] interceptors() default {};
	Class<?> requestFactory() default Void.class;
}
