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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Use this annotation to inject an HttpClient instance with the specified
 * KeyStore and TrustStore configured to perform an <b>HTTPS</b> request.
 * </p>
 * <p>
 * All the parameters are optional:
 * </p>
 * <ul>
 * <li><i>trustStore</i>: int, Resource id of your trust store file ex
 * <code>R.raw.cacerts.bks</code> Typically your servers trusted certificates
 * (public key, Root Chain Authority etc)</li>
 * 
 * <li><i>trustStorePwd</i>: String, Your trust store password (default is
 * <code>changeit</code>)</li>
 * 
 * <li><i>keyStore</i>: int, Resource id of your keystore Usually your private
 * key (client certificate)</li>
 * 
 * <li><i>keyStorePwd</i>: String, Your KeyStore password (default is
 * <code>changeit</code>)</li>
 * 
 * <li><i>allowAllHostnames</i>: boolean, if true, authorizes any TLS/SSL
 * hostname (default <code>true</code>) If false, Hostname in certificate (DN)
 * must match the URL.</li>
 * </ul>
 * 
 * <b>Note</b>:
 * <tt>Prior to ICS, Android accepts [Key|Trust]store only in BKS format
 * (Bouncycastle Key Store)</tt>
 * 
 * <blockquote>
 * 
 * Example :
 * 
 * <pre>
 * &#064;EBean
 * public class MyBean {
 * 	&#064;HttpsClient(trustStore = R.raw.cacerts, //
 * 	trustStorePwd = &quot;changeit&quot;, //
 * 	keyStore = R.raw.client, //
 * 	keyStorePwd = &quot;secret&quot;, //
 * 	allowAllHostnames = false)
 * 	HttpClient httpsClient;
 * 
 * 	&#064;AfterInject
 * 	&#064;Background
 * 	public void securedRequest() {
 * 		try {
 * 			HttpGet httpget = new HttpGet(&quot;https://www.verisign.com/&quot;);
 * 			HttpResponse response = httpsClient.execute(httpget);
 * 			doSomethingWithResponse(response);
 * 		} catch (Exception e) {
 * 			e.printStackTrace();
 * 		}
 * 	}
 * 
 * 	&#064;UiThread
 * 	public void doSomethingWithResponse(HttpResponse resp) {
 * 		Toast.makeText(this, &quot;HTTP status &quot; + resp.getStatusLine().getStatusCode(), Toast.LENGTH_LONG).show();
 * 	}
 * }
 * </pre>
 * 
 * </blockquote>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface HttpsClient {

	/**
	 * The default value of {@link #trustStorePwd()} and {@link #keyStorePwd()}.
	 */
	String DEFAULT_PASSWD = "changeit";

	/**
	 * The R.id.* field id which refers to the trust store file.
	 * 
	 * @return the id of the trust store file
	 */
	int trustStore() default ResId.DEFAULT_VALUE;

	/**
	 * The resource name which refers to the trust store file.
	 * 
	 * @return the resource name of the trust store file.
	 */
	String trustStoreResName() default "";

	/**
	 * The trust store password.
	 * 
	 * @return the trust store password
	 */
	String trustStorePwd() default DEFAULT_PASSWD;

	/**
	 * The R.id.* field id which refers to the key store file.
	 * 
	 * @return the id of the key store file
	 */
	int keyStore() default ResId.DEFAULT_VALUE;

	/**
	 * The resource name which refers to the key store file.
	 * 
	 * @return the resource name of the key store file
	 */
	String keyStoreResName() default "";

	/**
	 * The key store password.
	 * 
	 * @return the key store password
	 */
	String keyStorePwd() default DEFAULT_PASSWD;

	/**
	 * Whether to authorizes any TLS/SSL hostname.
	 * 
	 * @return <b>true</b> if authorizes any TLS/SSL hostname, <b>false</b>
	 *         otherwise.
	 */
	boolean allowAllHostnames() default true;
}
