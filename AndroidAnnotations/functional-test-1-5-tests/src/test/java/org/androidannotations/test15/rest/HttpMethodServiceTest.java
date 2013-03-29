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
package org.androidannotations.test15.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(AndroidAnnotationsTestRunner.class)
public class HttpMethodServiceTest {
	
	@Test
	public void use_delete_http_method() {
		HttpMethodsService_ service = new HttpMethodsService_();

		RestTemplate restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);

		service.delete();

		verify(restTemplate).exchange(Mockito.anyString(), Mockito.<HttpMethod> eq(HttpMethod.DELETE), Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any());
	}

	@Test
	public void use_get_http_method() {
		HttpMethodsService_ service = new HttpMethodsService_();

		RestTemplate restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);

		service.get();

		verify(restTemplate).exchange(Mockito.anyString(), Mockito.<HttpMethod> eq(HttpMethod.GET), Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void use_head_http_method() {
		HttpMethodsService_ service = new HttpMethodsService_();

		RestTemplate restTemplate = mock(RestTemplate.class);
		ResponseEntity<Object> response = mock(ResponseEntity.class);
		when(restTemplate.exchange("http://company.com/ajax/services/head/", HttpMethod.HEAD, null, null)).thenReturn(response);

		service.setRestTemplate(restTemplate);

		service.head();

		verify(restTemplate).exchange(Mockito.anyString(), Mockito.<HttpMethod> eq(HttpMethod.HEAD), Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void use_options_http_method() {
		HttpMethodsService_ service = new HttpMethodsService_();

		RestTemplate restTemplate = mock(RestTemplate.class);
		ResponseEntity<Object> response = mock(ResponseEntity.class);
		when(restTemplate.exchange("http://company.com/ajax/services/options/", HttpMethod.OPTIONS, null, null)).thenReturn(response);
		HttpHeaders headers = mock(HttpHeaders.class);
		when(response.getHeaders()).thenReturn(headers);
		
		service.setRestTemplate(restTemplate);

		service.options();

		verify(restTemplate).exchange(Mockito.anyString(), Mockito.<HttpMethod> eq(HttpMethod.OPTIONS), Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any());
	}

	@Test
	public void use_post_http_method() {
		HttpMethodsService_ service = new HttpMethodsService_();

		RestTemplate restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);

		service.post();

		verify(restTemplate).exchange(Mockito.anyString(), Mockito.<HttpMethod> eq(HttpMethod.POST), Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any());
	}

	@Test
	public void use_put_http_method() {
		HttpMethodsService_ service = new HttpMethodsService_();

		RestTemplate restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);

		service.put();

		verify(restTemplate).exchange(Mockito.anyString(), Mockito.<HttpMethod> eq(HttpMethod.PUT), Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any());
	}

}
