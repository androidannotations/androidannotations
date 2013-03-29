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

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.xtremelabs.robolectric.Robolectric;

@RunWith(AndroidAnnotationsTestRunner.class)
public class MyServiceTest {

	private MyService_ myService = new MyService_();

	private void addPendingResponse(String jsonResponse) {
		Robolectric.addPendingHttpResponse(HttpStatus.OK.value(), jsonResponse.replaceAll("'", "\""), new BasicHeader("content-type", "application/json"));
	}

	@Test
	public void can_override_root_url() {
		MyService_ myService = new MyService_();

		RestTemplate restTemplate = mock(RestTemplate.class);
		myService.setRestTemplate(restTemplate);
		myService.setRootUrl("http://newRootUrl");

		myService.removeEvent(42);

		verify(restTemplate).exchange(startsWith("http://newRootUrl"), Mockito.<HttpMethod> any(), Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any(), Mockito.<Map<String, ?>> any());
	}

	@Test
	public void getEventsArray2() {
		addPendingResponse("[{'id':1,'name':'event1'},{'id':2,'name':'event2'}]");
		ResponseEntity<Event[]> responseEntity = myService.getEventsArray2("test", 42);
		Event[] events = responseEntity.getBody();

		Event event1 = new Event(1, "event1");
		Event event2 = new Event(2, "event2");

		assertEquals(2, events.length);
		assertEquals(event1, events[0]);
		assertEquals(event2, events[1]);
	}

	@Test
	public void getEventsGenericsList() {
		addPendingResponse("[{'id':1,'name':'event1'},{'id':2,'name':'event2'}]");
		List<Event> events = myService.getEventsGenericsList("test", 42);

		Event event1 = new Event(1, "event1");
		Event event2 = new Event(2, "event2");

		assertEquals(2, events.size());
		assertEquals(event1, events.get(0));
		assertEquals(event2, events.get(1));
	}

	@Test
	public void getEventsGenericsArrayList() {
		addPendingResponse("[[{'id':1,'name':'event1'},{'id':2,'name':'event2'}],[{'id':3,'name':'event3'}]]");
		List<Event>[] events = myService.getEventsGenericsArrayList("test", 42);

		Event event1 = new Event(1, "event1");
		Event event2 = new Event(2, "event2");
		Event event3 = new Event(3, "event3");

		assertEquals(2, events.length);
		assertEquals(event1, events[0].get(0));
		assertEquals(event2, events[0].get(1));
		assertEquals(event3, events[1].get(0));
	}

	@Test
	public void getEventsGenericsListListEvent() {
		addPendingResponse("[[{'id':1,'name':'event1'},{'id':2,'name':'event2'}],[{'id':3,'name':'event3'}]]");
		List<List<Event>> events = myService.getEventsGenericsListListEvent("test", 42);

		Event event1 = new Event(1, "event1");
		Event event2 = new Event(2, "event2");
		Event event3 = new Event(3, "event3");

		assertEquals(2, events.size());
		assertEquals(event1, events.get(0).get(0));
		assertEquals(event2, events.get(0).get(1));
		assertEquals(event3, events.get(1).get(0));
	}

	@Test
	public void getEventsGenericsListListEvents() {
		addPendingResponse("[[[{'id':1,'name':'event1'}],[{'id':2,'name':'event2'}]],[[{'id':3,'name':'event3'}]]]");
		List<List<Event[]>> events = myService.getEventsGenericsListListEvents("test", 42);

		Event event1 = new Event(1, "event1");
		Event event2 = new Event(2, "event2");
		Event event3 = new Event(3, "event3");

		assertEquals(2, events.size());
		assertEquals(event1, events.get(0).get(0)[0]);
		assertEquals(event2, events.get(0).get(1)[0]);
		assertEquals(event3, events.get(1).get(0)[0]);
	}

	@Test
	public void getEventsGenericsMap() {
		addPendingResponse("{'event1':{'id':1,'name':'event1'},'event2':{'id':2,'name':'event2'}}");
		Map<String, Event> eventsMap = myService.getEventsGenericsMap("test", 42);

		Event event1 = new Event(1, "event1");
		Event event2 = new Event(2, "event2");

		assertEquals(2, eventsMap.size());
		assertEquals(event1, eventsMap.get("event1"));
		assertEquals(event2, eventsMap.get("event2"));
	}

	@Test
	public void urlWithAParameterDeclaredTwiceTest() {
		addPendingResponse("[[{'id':1,'name':'event1'},{'id':2,'name':'event2'}],[{'id':1,'name':'event1'},{'id':2,'name':'event2'}]]");
		Event[][] results = myService.urlWithAParameterDeclaredTwice(1985);

		Event event1 = new Event(1, "event1");
		Event event2 = new Event(2, "event2");
		Event[][] events = new Event[][] {{event1, event2}, {event1, event2}};

		for (int i = 0 ; i < events.length ; i++) {

			assertEquals(results[i].length, events[i].length);

			for (int j = 0 ; j < events[i].length ; j++) {
				assertEquals(events[i][j].getName(), results[i][j].getName());
				assertEquals(events[i][j].getId(), results[i][j].getId());
			}
		}
	}

}
