package com.googlecode.androidannotations.test15;

import org.apache.http.client.HttpClient;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.HttpsClient;

@EActivity
public class SSLConnection extends Activity {

	@HttpsClient(trustStore = R.raw.cacerts)
	HttpClient mHttpsClientTest1;

	@HttpsClient(trustStore = R.raw.cacerts, hostnameVerif = true)
	HttpClient mHttpsClientTest2;

	@HttpsClient
	HttpClient mHttpsClientTest3;

}
