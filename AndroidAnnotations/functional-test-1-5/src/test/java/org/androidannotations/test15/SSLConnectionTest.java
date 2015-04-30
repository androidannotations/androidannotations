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
package org.androidannotations.test15;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.Security;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SSLConnectionTest {

	private SSLConnection_ activity;

	@BeforeClass
	public static void addSecurityProvider() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Before
	public void setUp() {
		activity = Robolectric.buildActivity(SSLConnection_.class).create().get();
	}

	@Test
	public void truststoreProvided() {
		assertNotNull(activity.mHttpsClientTest1);
		ClientConnectionManager ccm = activity.mHttpsClientTest1.getConnectionManager();
		assertNotNull(ccm);

		Scheme httpsScheme = ccm.getSchemeRegistry().getScheme("https");
		assertNotNull(httpsScheme);

		assertEquals(443, httpsScheme.getDefaultPort());
		SocketFactory socketFactHttps = httpsScheme.getSocketFactory();

		if (!(socketFactHttps instanceof SSLSocketFactory)) {
			fail("wrong instance should be org.apache.http.conn.ssl.SSLSocketFactory, getting " + socketFactHttps);
		}
		assertEquals(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER, ((SSLSocketFactory) socketFactHttps).getHostnameVerifier());
	}

	@Test
	public void strictHostnameVerifier() {
		assertNotNull(activity.mHttpsClientTest2);
		ClientConnectionManager ccm = activity.mHttpsClientTest2.getConnectionManager();
		Scheme httpsScheme = ccm.getSchemeRegistry().getScheme("https");
		SSLSocketFactory socketFactHttps = (SSLSocketFactory) httpsScheme.getSocketFactory();

		assertEquals(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER, socketFactHttps.getHostnameVerifier());
	}

	@Test
	public void noOptions() {
		assertNotNull(activity.mHttpsClientTest3);
		ClientConnectionManager ccm = activity.mHttpsClientTest3.getConnectionManager();
		assertNotNull(ccm);
	}
}
