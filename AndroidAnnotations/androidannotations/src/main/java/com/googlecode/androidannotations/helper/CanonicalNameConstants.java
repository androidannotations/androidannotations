/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.helper;

public final class CanonicalNameConstants {

	/**
	 * Android libraries
	 */
	public static final String LOG = "android.util.Log";
	public static final String PARCELABLE = "android.os.Parcelable";
	public static final String INTENT = "android.content.Intent";
	public static final String BUNDLE = "android.os.Bundle";

	/**
	 * Java libraries
	 */
	public static final String URI = "java.net.URI";
	public static final String SET = "java.util.Set";
	public static final String COLLECTIONS = "java.util.Collections";
	public static final String STRING = "java.lang.String";

	/**
	 * SpringFramework libraries
	 */
	public static final String RESPONSE_ENTITY = "org.springframework.http.ResponseEntity";
	public static final String HTTP_HEADERS = "org.springframework.http.HttpHeaders";
	public static final String MEDIA_TYPE = "org.springframework.http.MediaType";
	public static final String HTTP_METHOD = "org.springframework.http.HttpMethod";
	public static final String HTTP_ENTITY = "org.springframework.http.HttpEntity";

	private CanonicalNameConstants() {

	}

}
