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

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.HttpsClient;
import org.apache.http.client.HttpClient;

import android.app.Activity;

@EActivity
public class SSLConnection extends Activity {

	@HttpsClient(trustStore = R.raw.cacerts)
	HttpClient mHttpsClientTest1;

	@HttpsClient(trustStore = R.raw.cacerts, allowAllHostnames = false)
	HttpClient mHttpsClientTest2;

	@HttpsClient
	HttpClient mHttpsClientTest3;

	@HttpsClient(trustStoreResName = "cacerts", keyStoreResName = "cacerts")
	HttpClient mHttpsClientTest4;

}
