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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to inject an HttpClient instance with the specified
 * KeyStore and TrustStore configured to perform an <b>HTTPS</b> request.
 * <p/>
 * All the parameters are optional:
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
 * <p/>
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
	public static final String DEFAULT_PASSWD = "changeit";

	int trustStore() default ResId.DEFAULT_VALUE;

	String trustStorePwd() default DEFAULT_PASSWD;

	int keyStore() default ResId.DEFAULT_VALUE;

	String keyStorePwd() default DEFAULT_PASSWD;

	boolean allowAllHostnames() default true;
}
