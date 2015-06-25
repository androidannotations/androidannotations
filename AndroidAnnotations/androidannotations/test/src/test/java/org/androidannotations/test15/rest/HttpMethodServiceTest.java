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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.robolectric.RobolectricTestRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(RobolectricTestRunner.class)
public class HttpMethodServiceTest {

	@Test
	public void useDeleteHttpMethod() {
		HttpMethodsService_ service = new HttpMethodsService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);

		service.delete();

		verify(restTemplate).exchange(Matchers.anyString(), Matchers.<HttpMethod> eq(HttpMethod.DELETE), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any());
	}

	@Test
	public void useGetHttpMethod() {
		HttpMethodsService_ service = new HttpMethodsService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);

		service.get();

		verify(restTemplate).exchange(Matchers.anyString(), Matchers.<HttpMethod> eq(HttpMethod.GET), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void useHeadHttpMethod() {
		HttpMethodsService_ service = new HttpMethodsService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		ResponseEntity<Object> response = mock(ResponseEntity.class);
		when(restTemplate.exchange(Matchers.anyString(), Matchers.<HttpMethod> eq(HttpMethod.HEAD), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any())).thenReturn(response);

		service.setRestTemplate(restTemplate);

		service.head();

		verify(restTemplate).exchange(Matchers.anyString(), Matchers.<HttpMethod> eq(HttpMethod.HEAD), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void useOptionsHttpMethod() {
		HttpMethodsService_ service = new HttpMethodsService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		ResponseEntity<Object> response = mock(ResponseEntity.class);
		when(restTemplate.exchange(Matchers.anyString(), Matchers.<HttpMethod> eq(HttpMethod.OPTIONS), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any())).thenReturn(response);
		HttpHeaders headers = mock(HttpHeaders.class);
		when(response.getHeaders()).thenReturn(headers);

		service.setRestTemplate(restTemplate);

		service.options();

		verify(restTemplate).exchange(Matchers.anyString(), Matchers.<HttpMethod> eq(HttpMethod.OPTIONS), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any());
	}

	@Test
	public void usePostHttpMethod() {
		HttpMethodsService_ service = new HttpMethodsService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);

		service.post();

		verify(restTemplate).exchange(Matchers.anyString(), Matchers.<HttpMethod> eq(HttpMethod.POST), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any());
	}

	@Test
	public void usePutHttpMethod() {
		HttpMethodsService_ service = new HttpMethodsService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);

		service.put();

		verify(restTemplate).exchange(Matchers.anyString(), Matchers.<HttpMethod> eq(HttpMethod.PUT), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any());
	}

}
