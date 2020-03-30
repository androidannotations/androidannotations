/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.rest.spring.test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.robolectric.RobolectricTestRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(RobolectricTestRunner.class)
public class PostRestServiceTest {

	PostRestService_ service;

	RestTemplate restTemplate;

	@Before
	public void setUp() {
		service = new PostRestService_(null);
		restTemplate = mock(RestTemplate.class);
		service.setRestTemplate(restTemplate);
	}

	@Test
	public void injectsPostParametersIntoRequestEntity() {
		service.post("first", "second", "last");

		verifyPostParametersAdded();
	}

	@Test
	public void injectsMultipartPostParametersIntoRequestEntity() {
		service.multipart("first", "second", "last");

		verifyPostParametersAdded();
	}

	private void verifyPostParametersAdded() {
		LinkedMultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
		postParameters.add("thirdParam", "last");
		postParameters.add("otherParam", "first");
		postParameters.add("postParam", "second");

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(postParameters);

		verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(equals(requestEntity)), ArgumentMatchers.<Class<Object>> any());
	}

	private static <T> ArgumentMatcher<HttpEntity<T>> equals(final HttpEntity<T> expected) {
		return new ArgumentMatcher<HttpEntity<T>>() {

			@Override
			public boolean matches(HttpEntity<T> argument) {
				return expected.getBody().equals(argument.getBody());
			}
		};
	}

}
