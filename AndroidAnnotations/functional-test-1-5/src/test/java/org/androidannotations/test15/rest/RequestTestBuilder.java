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
package org.androidannotations.test15.rest;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.AssertionFailedError;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

public class RequestTestBuilder {
	private MyService_ myService = new MyService_(null);
	private HttpEntityArgumentMatcher entityArgumentMatcher = new HttpEntityArgumentMatcher();;
	private RestTemplate restTemplate;

	private Map<String, String> requestHeaders = new HashMap<String, String>();
	private Map<String, String> requestCookies = new HashMap<String, String>();
	private Map<String, String> responseCookies = new HashMap<String, String>();
	private String responseContent;
	private boolean hasUrlVariables;

	public static RequestTestBuilder build() {
		return new RequestTestBuilder();
	}

	public RequestTestBuilder() {
		restTemplate = Mockito.spy(myService.getRestTemplate());
		myService.setRestTemplate(restTemplate);
	}

	public RequestTestBuilder requestHeader(String name, String value) {
		requestHeaders.put(name, value);
		myService.setHeader(name, value);
		return this;
	}

	public RequestTestBuilder requestCookie(String name, String value) {
		requestCookies.put(name, value);
		myService.setCookie(name, value);
		return this;
	}

	public RequestTestBuilder responseCookie(String name, String value) {
		responseCookies.put(name, value);
		return this;
	}

	public RequestTestBuilder responseContent(String responseContent) {
		this.responseContent = responseContent;
		return this;
	}

	public RequestTestBuilder hasUrlVariables(boolean hasUrlVariables) {
		this.hasUrlVariables = hasUrlVariables;
		return this;
	}

	public void asserts(RequestTestBuilderExecutor executor) {
		// Prepare fake response
		prepareFakeResponse();

		// Make the call
		executor.execute(myService);

		// Checks
		checkRequest();
		checkResponseCookies();
	}

	private void prepareFakeResponse() {
		Set<String> cookiesNames = responseCookies.keySet();
		Header[] headers = new Header[1 + responseCookies.size()];
		headers[0] = new BasicHeader("content-type", "application/json");

		int i = 1;
		for (String cookieName : cookiesNames) {
			headers[i++] = new BasicHeader("Set-Cookie", cookieName + "=" + responseCookies.get(cookieName));
		}

		String responseBody = responseContent != null ? responseContent.replaceAll("'", "\"") : "";

		Robolectric.addPendingHttpResponse(HttpStatus.OK.value(), responseBody, headers);
	}

	private void checkRequest() {
		if (hasUrlVariables) {
			verify(restTemplate).exchange(Matchers.anyString(), //
					Matchers.any(HttpMethod.class), //
					argThat(entityArgumentMatcher), //
					Matchers.<Class<Object>> any(), //
					Matchers.<Map<String, Object>> any());
		} else {
			verify(restTemplate).exchange(Matchers.anyString(), //
					Matchers.any(HttpMethod.class), //
					argThat(entityArgumentMatcher), //
					Matchers.<Class<Object>> any());
		}
	}

	private void checkResponseCookies() throws AssertionFailedError {
		for (String responseCookieName : responseCookies.keySet()) {
			String cookieValue = myService.getCookie(responseCookieName);
			if (cookieValue == null || !cookieValue.equals(responseCookies.get(responseCookieName))) {
				throw new AssertionFailedError("Response cookie " + responseCookieName + " wasn't set");
			}
		}
	}

	public class HttpEntityArgumentMatcher extends ArgumentMatcher<HttpEntity<Void>> {
		@Override
		public boolean matches(Object argument) {
			HttpEntity<?> httpEntity = (HttpEntity<?>) argument;
			HttpHeaders httpHeaders = httpEntity.getHeaders();

			// Check that cookies set earlier are sent in the request
			if (requestCookies.size() > 0) {
				String[] httpCookies = httpHeaders.getFirst("Cookie").split(";");
				for (String cookieName : requestCookies.keySet()) {
					if (Arrays.binarySearch(httpCookies, cookieName + "=" + requestCookies.get(cookieName)) == -1) {
						throw new AssertionFailedError("The cookie " + cookieName + " is missing!");
					}
				}
			}

			// Check that headers set earlier are sent in the request
			for (String headerName : requestHeaders.keySet()) {
				if (!(httpHeaders.containsKey(headerName) && httpHeaders.getFirst(headerName).equals(requestHeaders.get(headerName)))) {
					throw new AssertionFailedError("The header " + headerName + " is missing!");
				}
			}

			return true;
		}
	}

	public interface RequestTestBuilderExecutor {
		void execute(MyService myService);
	}

}