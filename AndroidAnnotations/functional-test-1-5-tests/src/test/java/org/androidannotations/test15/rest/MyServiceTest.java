/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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

import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;

@RunWith(AndroidAnnotationsTestRunner.class)
public class MyServiceTest {

	@Test
	public void can_override_root_url() {
		MyService_ myService = new MyService_();
		
		RestTemplate restTemplate = mock(RestTemplate.class);
		myService.setRestTemplate(restTemplate);

		myService.setRootUrl("http://newRootUrl");
		
		myService.removeEvent(42);
		
		verify(restTemplate).exchange(startsWith("http://newRootUrl"), Mockito.<HttpMethod> any(), Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any(), Mockito.<Map<String, ?>>any());

	}
	
}
