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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Head;
import org.androidannotations.annotations.rest.Options;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.RequiresAuthentication;
import org.androidannotations.annotations.rest.RequiresCookie;
import org.androidannotations.annotations.rest.RequiresCookieInUrl;
import org.androidannotations.annotations.rest.RequiresHeader;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.annotations.rest.SetsCookie;
import org.androidannotations.api.rest.MediaType;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// if defined, the rootUrl will be added as a prefix to every request
@Rest(rootUrl = "http://company.com/ajax/services", converters = { MappingJacksonHttpMessageConverter.class, EBeanConverter.class },
		interceptors = { RequestInterceptor.class, EBeanInterceptor.class },
		requestFactory = MyRequestFactory.class)
public interface MyService {

	// *** GET ***

	// url variables are mapped to method parameter names.
	@RequiresCookie("xt")
	@Get("/events/{year}/{location}")
	@Accept(MediaType.APPLICATION_JSON)
	EventList getEvents(String location, int year);

	@Get("/events/{year}/{location}")
	@Accept("application/json")
	Event[] getEventsArray(String location, int year);

	@Get("/events/{year}/{year}")
	@Accept(MediaType.APPLICATION_JSON)
	Event[][] urlWithAParameterDeclaredTwice(int year);

	@Get("/events/{year}/{location}")
	@Accept(MediaType.APPLICATION_JSON)
	Event[][] getEventsArrayOfArrays(String location, int year);

	// The response can be a ResponseEntity<T>
	@Get("/events/{year}/{location}")
	/*
	 * You may (or may not) declare throwing RestClientException (as a reminder, since it's a RuntimeException), but nothing else.
	 */
	ResponseEntity<EventList> getEvents2(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	ResponseEntity<Event[]> getEventsArray2(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	ResponseEntity<Event[][]> getEventsArrayOfArrays2(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event> getEventsGenericsList(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event>[] getEventsGenericsArrayList(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<List<Event>> getEventsGenericsListListEvent(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<List<Event[]>> getEventsGenericsListListEvents(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event[]> getEventsGenericsListArray(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event[][]> getEventsGenericsListArrayArray(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	Set<Event> getEventsGenericsSet(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	GenericEvent<String> getEventsGenericString(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	GenericEvent<Integer> getEventsGenericInteger(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	GenericEvent<List<Event>> getEventsGenericListEvent(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	GenericEvent<GenericEvent<GenericEvent<String>>> getEventsGenericsInception(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	@SetsCookie({ "xt", "sjsaid" })
	Map<String, Event> getEventsGenericsMap(String location, int year) throws RestClientException;

	@RequiresCookie("sjsaid")
	@RequiresCookieInUrl("xt")
	@Get("/events/{year}/{location}?xt={xt}")
	void getEventsVoid(String location, int year) throws RestClientException;

	// *** POST ***
	@RequiresHeader("SomeFancyHeader")
	@Post("/login")
	@SetsCookie({ "xt", "sjsaid" })
	void authenticate();

	@RequiresAuthentication
	@Post("http://company.com/client/ping")
	void ping();

	// There should be max 1 parameter that is not mapped to an attribute. This
	// parameter will be used as the post entity.
	@Post("/events/")
	@Accept(MediaType.APPLICATION_JSON)
	Event addEvent(Event event);

	@Post("/events/{year}/")
	Event addEvent(Event event, int year);

	@Post("/events/{year}/")
	Event addEvent(int year);

	@Post("/events/")
	ResponseEntity<Event> addEvent2(Event event);

	/**
	 * Output different then input
	 */
	@Post("/events/")
	ResponseEntity<String> addEvent3(Event event);

	@Post("/events/")
	List<? extends Event> addEventGenericsListWildcardExtends(Event event);

	@Post("/events/")
	List<Event> addEventGenericsList(Event event);

	// TODO: Handle generics in params
	// @Post("/events/")
	// List<Event> addEventGenericsList(List<Event> events);

	@Post("/events/")
	Set<Event> addEventGenericsSet(Event event);

	@Post("/events/")
	GenericEvent<GenericEvent<GenericEvent<String>>> addEventGenericsInception(Event event);

	@Post("/events/")
	Map<String, Event> addEventGenericsMap(Event event);

	/**
	 * Output different then input
	 */
	@Post("/events/")
	String addEvent4(Event event);

	@Post("/events/")
	void addEvent5(Event event);

	@Post("/events/{year}/")
	@Accept(MediaType.APPLICATION_JSON)
	ResponseEntity<Event> addEvent2(Event event, int year);

	// *** PUT ***

	@Put("/events/{id}")
	void updateEvent(Event event, int id);

	@Put("/events/{date}")
	void updateEvent(long date);

	@Put("/events/{date}")
	Event updateEventWithResponse(long date);

	@Put("/events/{date}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	Event updateEventWithRequires(long date);

	// *** DELETE ***

	@Delete("/events/{id}")
	void removeEvent(long id);

	@Delete("/events/{id}")
	Event removeEventWithResponse(long id);

	@Delete("/events/{id}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	void removeEventWithRequires(long id);

	@Delete("/events/{id}")
	@RequiresAuthentication
	void removeEventWithAuthentication(long id);

	// *** HEAD ***

	@Head("/events/{year}/{location}")
	HttpHeaders getEventHeaders(String location, int year);

	@Head("/events/{date}")
	HttpHeaders getEventheaders(long date);

	@Head("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	HttpHeaders getEventheadersWithRequires(long date);

	@Head("/events/{date}")
	@RequiresAuthentication
	HttpHeaders getEventheadersWithAuthentication(long date);

	// *** OPTIONS ***

	@Options("/events/{year}/{location}")
	Set<HttpMethod> getEventOptions(String location, int year);

	@Options("/events/{date}")
	Set<HttpMethod> getEventOptions(long date);

	@Options("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	Set<HttpMethod> getEventOptionsWithRequires(long date);

	@Options("/events/{date}")
	@RequiresAuthentication
	Set<HttpMethod> getEventOptionsWithAuthentication(long date);

	// if you need to add some configuration to the Spring RestTemplate.
	RestTemplate getRestTemplate();

	void setRestTemplate(RestTemplate restTemplate);

	void setRootUrl(String test);

	String getRootUrl();

	void setCookie(String cookieName, String value);

	String getCookie(String cookieName);

	void setHeader(String headerName, String value);

	String getHeader(String headerName);

	void setAuthentication(HttpAuthentication auth);

	void setHttpBasicAuth(String username, String password);

	void setBearerAuth(String token);
}
