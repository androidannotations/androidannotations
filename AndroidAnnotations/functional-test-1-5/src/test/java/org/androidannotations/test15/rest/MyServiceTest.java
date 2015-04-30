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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.test15.rest.RequestTestBuilder.RequestTestBuilderExecutor;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(RobolectricTestRunner.class)
public class MyServiceTest {

	private MyService_ myService = new MyService_(null);

	private void addPendingResponse(String jsonResponse) {
		addPendingResponse(jsonResponse, "_=_");
	}

	private void addPendingResponse(String jsonResponse, String... cookies) {
		Header[] headers = new Header[1 + cookies.length / 2];
		headers[0] = new BasicHeader("content-type", "application/json");
		for (int i = 0, j = 1; i < cookies.length - 1; i += 2, j++) {
			headers[j] = new BasicHeader("set-cookie", cookies[i] + "=" + cookies[i + 1]);
		}
		Robolectric.addPendingHttpResponse(HttpStatus.OK.value(), jsonResponse.replaceAll("'", "\""), headers);
	}

	@Test
	public void canOverrideRootUrl() {
		MyService_ myService = new MyService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		myService.setRestTemplate(restTemplate);
		myService.setRootUrl("http://newRootUrl");

		myService.removeEvent(42);

		verify(restTemplate).exchange(startsWith("http://newRootUrl"), Matchers.<HttpMethod> any(), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any(), Matchers.<Map<String, ?>> any());
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
		Event[][] events = new Event[][] { { event1, event2 }, { event1, event2 } };

		for (int i = 0; i < events.length; i++) {

			assertEquals(results[i].length, events[i].length);

			for (int j = 0; j < events[i].length; j++) {
				assertEquals(events[i][j].getName(), results[i][j].getName());
				assertEquals(events[i][j].getId(), results[i][j].getId());
			}
		}
	}

	@Test
	public void manualFullUrl() {
		MyService_ myService = new MyService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		myService.setRestTemplate(restTemplate);

		// make sure we used the full custom url.
		// this may be used like in Google's APIs
		// to fetch an oauth token; Mockito doesn't
		// return a response with the mock'd template,
		// so we just use this weird "ping" endpoint
		addPendingResponse("fancyHeaderToken");
		myService.setHttpBasicAuth("fancyUser", "fancierPassword");
		myService.ping();
		verify(restTemplate).exchange(eq("http://company.com/client/ping"), Matchers.<HttpMethod> any(), Matchers.<HttpEntity<?>> any(), Matchers.<Class<Object>> any());
	}

	@Test
	public void cookieInUrl() {
		final String xtValue = "1234";
		final String sjsaidValue = "7890";
		final String locationValue = "somePlace";
		final int yearValue = 2013;

		MyService_ myService = new MyService_(null);

		RestTemplate restTemplate = mock(RestTemplate.class);
		myService.setRestTemplate(restTemplate);

		addPendingResponse("{'id':1,'name':'event1'}");

		// normally this is set by a call like authenticate()
		// which is annotated with @SetsCookie
		myService.setCookie("xt", xtValue);
		myService.setCookie("sjsaid", sjsaidValue);
		myService.setHttpBasicAuth("fancyUser", "fancierPassword");
		myService.getEventsVoid(locationValue, yearValue);

		ArgumentMatcher<HttpEntity<Void>> matcher = new ArgumentMatcher<HttpEntity<Void>>() {

			@Override
			public boolean matches(Object argument) {
				final String expected = "sjsaid=" + sjsaidValue + ";";
				return expected.equals(((HttpEntity<?>) argument).getHeaders().get("Cookie").get(0));
			}
		};

		Map<String, Object> urlVariables = new HashMap<String, Object>();
		urlVariables.put("location", locationValue);
		urlVariables.put("year", yearValue);
		urlVariables.put("xt", xtValue);
		verify(restTemplate).exchange(Matchers.anyString(), Matchers.<HttpMethod> any(), argThat(matcher), Matchers.<Class<Object>> any(), eq(urlVariables));
	}

	@Test
	public void authenticate() {
		RequestTestBuilder.build() //
				.requestHeader("SomeFancyHeader", "aFancyHeader") //
				.responseCookie("xt", "1234") //
				.responseCookie("sjsaid", "5678") //
				.asserts(new RequestTestBuilderExecutor() {
					@Override
					public void execute(MyService myService) {
						myService.authenticate();
					}
				});
	}

	@Test
	public void removeEventWithRequires() {
		RequestTestBuilder.build() //
				.requestCookie("myCookie", "myCookieValue") //
				.requestHeader("SomeFancyHeader", "aFancyHeader") //
				.hasUrlVariables(true) //
				.asserts(new RequestTestBuilderExecutor() {
					@Override
					public void execute(MyService myService) {
						myService.removeEventWithRequires(0);
					}
				});
	}

	@Test
	public void updateEventWithRequires() {
		RequestTestBuilder.build() //
				.requestCookie("myCookie", "myCookieValue") //
				.requestHeader("SomeFancyHeader", "aFancyHeader") //
				.responseContent("{'id':1,'name':'event1'}") //
				.hasUrlVariables(true) //
				.asserts(new RequestTestBuilderExecutor() {
					@Override
					public void execute(MyService myService) {
						myService.updateEventWithRequires(0);
					}
				});
	}

}
