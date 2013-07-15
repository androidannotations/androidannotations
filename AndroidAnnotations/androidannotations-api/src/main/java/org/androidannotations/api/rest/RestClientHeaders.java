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
package org.androidannotations.api.rest;

import org.springframework.http.HttpAuthentication;

/**
 * A @Rest interface implementing this interface will automatically have the
 * implementations of these methods generated.
 */
public interface RestClientHeaders {

	/**
	 * Gets a cookie by name.
	 * 
	 * @param name
	 * @return the cookie value.
	 */
	String getCookie(String name);

	/**
	 * Sets a cookie by name.
	 * 
	 * @param name
	 * @param value
	 */
	void setCookie(String name, String value);

	/**
	 * Gets a header by name.
	 * 
	 * @param name
	 * @return the header value.
	 */
	String getHeader(String name);

	/**
	 * Sets a header by name.
	 * 
	 * @param name
	 * @param value
	 */
	void setHeader(String name, String value);

	/**
	 * Sets the authentication object.
	 * 
	 * @param auth
	 */
	void setAuthentication(HttpAuthentication auth);

	/**
	 * Sets the basic authentication user/password.
	 * 
	 * @param user
	 * @param password
	 */
	void setHttpBasicAuth(String user, String password);
}
