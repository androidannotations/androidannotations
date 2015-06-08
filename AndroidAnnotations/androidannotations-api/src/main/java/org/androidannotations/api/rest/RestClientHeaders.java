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
package org.androidannotations.api.rest;

import org.springframework.http.HttpAuthentication;

/**
 * A @Rest interface implementing this interface will automatically have the
 * implementations of these methods generated.
 * 
 * @see org.androidannotations.annotations.rest.Rest
 * @see org.androidannotations.annotations.rest.RequiresCookie
 * @see org.androidannotations.annotations.rest.RequiresHeader
 * @see org.androidannotations.annotations.rest.RequiresAuthentication
 */
public interface RestClientHeaders {

	/**
	 * Gets a cookie by name.
	 * 
	 * @param name
	 *            Name of the cookie
	 * @return the cookie value.
	 * 
	 * @see org.androidannotations.annotations.rest.RequiresCookie
	 */
	String getCookie(String name);

	/**
	 * Sets a cookie by name.
	 * 
	 * @param name
	 *            Name of the cookie
	 * @param value
	 *            Value of the cookie
	 * 
	 * @see org.androidannotations.annotations.rest.RequiresCookie
	 */
	void setCookie(String name, String value);

	/**
	 * Gets a header by name.
	 * 
	 * @param name
	 *            Name of the header
	 * @return the header value.
	 * 
	 * @see org.androidannotations.annotations.rest.RequiresHeader
	 */
	String getHeader(String name);

	/**
	 * Sets a header by name.
	 * 
	 * @param name
	 *            Name of the header
	 * @param value
	 *            Value of the header
	 * 
	 * @see org.androidannotations.annotations.rest.RequiresHeader
	 */
	void setHeader(String name, String value);

	/**
	 * Sets the authentication object.
	 * 
	 * @param auth
	 *            Authentication data
	 * 
	 * @see org.androidannotations.annotations.rest.RequiresAuthentication
	 */
	void setAuthentication(HttpAuthentication auth);

	/**
	 * Sets the basic authentication user/password.
	 * 
	 * @param user
	 *            Name of the user
	 * @param password
	 *            Password of the user
	 * 
	 * @see org.androidannotations.annotations.rest.RequiresAuthentication
	 */
	void setHttpBasicAuth(String user, String password);

	/**
	 * Sets the Authorization: Bearer header as documented in RFC6750
	 *
	 * @param token
	 *            Token used for authentication
	 * 
	 * @see org.androidannotations.annotations.rest.RequiresAuthentication
	 */
	void setBearerAuth(String token);
}
