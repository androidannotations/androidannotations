/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
 * Use this annotation to get an HttpClient instance with the specified KeyStore
 * and TrustStore configured to perform an <b>HTTPS</b> request <br/>
 * <br/>
 * 
 * All the parameters are optional<br/>
 * <br/>
 * 
 * <i>trustStore</i>: int, Resource id of your trust store file ex
 * <code>R.raw.cacerts.bks</code> Typically your servers trusted certificates
 * (public key, Root Chain Authority etc) <br/>
 * <br/>
 * 
 * <i>trustStorePwd</i>: String, Your trust store password (default is
 * <code>changeit</code>) <br/>
 * <br/>
 * 
 * <i>keyStore</i>: int, Resource id of your keystore Usually your private key
 * (client certificate) <br/>
 * <br/>
 * 
 * <i>keyStorePwd</i>: String, Your KeyStore password (default is
 * <code>changeit</code>) <br/>
 * <br/>
 * 
 * <i>allowAllHostnames</i>: boolean, if true, authorizes any TLS/SSL hostname
 * (default <code>true</code>) If false, Hostname in certificate (DN) must match
 * the URL.<br/>
 * <br/>
 * 
 * <b>Note</b>:
 * <tt>Prior to ICS, Android accepts [Key|Trust]store only in BKS format
 * (Bouncycastle Key Store)</tt>
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
