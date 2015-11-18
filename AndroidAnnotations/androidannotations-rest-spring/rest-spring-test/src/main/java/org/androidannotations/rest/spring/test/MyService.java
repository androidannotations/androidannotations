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
package org.androidannotations.rest.spring.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.androidannotations.rest.spring.annotations.Accept;
import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Delete;
import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Head;
import org.androidannotations.rest.spring.annotations.Header;
import org.androidannotations.rest.spring.annotations.Headers;
import org.androidannotations.rest.spring.annotations.Options;
import org.androidannotations.rest.spring.annotations.Part;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.RequiresAuthentication;
import org.androidannotations.rest.spring.annotations.RequiresCookie;
import org.androidannotations.rest.spring.annotations.RequiresCookieInUrl;
import org.androidannotations.rest.spring.annotations.RequiresHeader;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.annotations.SetsCookie;
import org.androidannotations.rest.spring.api.MediaType;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// if defined, the rootUrl will be added as a prefix to every request
@Rest(rootUrl = "http://company.com/ajax/services", converters = { MappingJacksonHttpMessageConverter.class, EBeanConverter.class, FormHttpMessageConverter.class }, //
				interceptors = { RequestInterceptor.class, EBeanInterceptor.class }, //
				requestFactory = MyRequestFactory.class)
public interface MyService {

	// *** GET ***

	// url variables are mapped to method parameter names.
	@RequiresCookie("xt")
	@Get("/events/{year}/{location}")
	@Accept(MediaType.APPLICATION_JSON)
	EventList getEvents(@Path String location, @Path int year);

	@Get("/events/{year}/{location}")
	@Accept("application/json")
	Event[] getEventsArray(@Path String location, @Path int year);

	@Get("/events/{year}/{year}")
	@Accept(MediaType.APPLICATION_JSON)
	Event[][] urlWithAParameterDeclaredTwice(@Path int year);

	@Get("/events/{year}/{location}")
	@Accept(MediaType.APPLICATION_JSON)
	Event[][] getEventsArrayOfArrays(@Path String location, @Path int year);

	// The response can be a ResponseEntity<T>
	@Get("/events/{year}/{location}")
	/*
	 * You may (or may not) declare throwing RestClientException (as a reminder, since it's a RuntimeException), but nothing else.
	 */
	ResponseEntity<EventList> getEvents2(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	ResponseEntity<Event[]> getEventsArray2(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	ResponseEntity<Event[][]> getEventsArrayOfArrays2(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event> getEventsGenericsList(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event>[] getEventsGenericsArrayList(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event>[][] getEventsGenericsArrayList2(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<List<Event>> getEventsGenericsListListEvent(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<List<Event[]>> getEventsGenericsListListEvents(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event[]> getEventsGenericsListArray(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	List<Event[][]> getEventsGenericsListArrayArray(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	Set<Event> getEventsGenericsSet(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	GenericEvent<String> getEventsGenericString(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	GenericEvent<Integer> getEventsGenericInteger(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	GenericEvent<List<Event>> getEventsGenericListEvent(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	GenericEvent<GenericEvent<GenericEvent<String>>> getEventsGenericsInception(@Path String location, @Path int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	@SetsCookie({ "xt", "sjsaid" })
	Map<String, Event> getEventsGenericsMap(@Path String location, @Path int year) throws RestClientException;

	@RequiresCookie("sjsaid")
	@RequiresCookieInUrl("xt")
	@Get("/events/{year}/{location}?xt={xt}")
	void getEventsVoid(@Path String location, @Path int year) throws RestClientException;

	// *** POST ***
	@RequiresHeader("SomeFancyHeader")
	@Post("/login")
	@SetsCookie({ "xt", "sjsaid" })
	void authenticate();

	@RequiresAuthentication
	@Post("http://company.com/client/ping")
	void ping();

	@Post("/events/")
	@Accept(MediaType.APPLICATION_JSON)
	Event addEvent(@Body String event);

	@Post("/events/{year}/")
	Event addEvent(@Body Event event, @Path int year);

	@Post("/events/{year}/")
	Event addEvent(@Path int year);

	@Post("/events/")
	ResponseEntity<Event> addEvent2(@Body Event event);

	@Post("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	void addEventWithParameters(@Path String date, @Field String parameter, @Field String otherParameter);

	@Post("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	void addEventWithParts(@Path String date, @Part String parameter, @Part String otherParameter);

	@Post("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	void addEventWithPathParameters(@Path("date") String pathParam, @Field String parameter);

	@Post("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@Header(name = "SomeFancyHeader", value = "fancy")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	void addEventWithHeaders(@Path String date, @Body String parameter);

	@Post("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@Headers(@Header(name = "SomeFancyHeader", value = "fancy"))
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	void addEventWithHeadersHeadersAnnotation(@Path String date, @Body String parameter);

	/**
	 * Output different then input
	 */
	@Post("/events/")
	ResponseEntity<String> addEvent3(@Body Event event);

	@Post("/events/")
	List<? extends Event> addEventGenericsListWildcardExtends(@Body Event event);

	@Post("/events/")
	List<Event> addEventGenericsList(@Body Event event);

	// TODO: Handle generics in params
	// @Post("/events/")
	// List<Event> addEventGenericsList(List<Event> events);

	@Post("/events/")
	Set<Event> addEventGenericsSet(@Body Event event);

	@Post("/events/")
	GenericEvent<GenericEvent<GenericEvent<String>>> addEventGenericsInception(@Body Event event);

	@Post("/events/")
	Map<String, Event> addEventGenericsMap(@Body Event event);

	/**
	 * Output different then input
	 */
	@Post("/events/")
	String addEvent4(@Body Event event);

	@Post("/events/")
	void addEvent5(@Body Event event);

	@Post("/events/{year}/")
	@Accept(MediaType.APPLICATION_JSON)
	ResponseEntity<Event> addEvent2(@Body Event event, @Path int year);

	// *** PUT ***

	@Put("/events/{id}")
	void updateEvent(@Body Event event, @Path int id);

	@Put("/events/{date}")
	void updateEvent(@Path long date);

	@Put("/events/{date}")
	Event updateEventWithResponse(@Path long date);

	@Put("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	Event updateEventWithRequires(@Path long date);

	// *** DELETE ***

	@Delete("/events/{id}")
	void removeEvent(@Path long id);

	@Delete("/events/{id}")
	Event removeEventWithResponse(@Path long id);

	@Delete("/events/{id}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	void removeEventWithRequires(@Path long id);

	@Delete("/events/{id}")
	@RequiresAuthentication
	void removeEventWithAuthentication(@Path long id);

	// *** HEAD ***

	@Head("/events/{year}/{location}")
	HttpHeaders getEventHeaders(@Path String location, @Path int year);

	@Head("/events/{date}")
	HttpHeaders getEventheaders(@Path long date);

	@Head("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	HttpHeaders getEventheadersWithRequires(@Path long date);

	@Head("/events/{date}")
	@RequiresAuthentication
	HttpHeaders getEventheadersWithAuthentication(@Path long date);

	// *** OPTIONS ***

	@Options("/events/{year}/{location}")
	Set<HttpMethod> getEventOptions(@Path String location, @Path int year);

	@Options("/events/{date}")
	Set<HttpMethod> getEventOptions(@Path long date);

	@Options("/events/{date}?myCookieInUrl={myCookieInUrl}")
	@RequiresHeader("SomeFancyHeader")
	@RequiresCookie("myCookie")
	@RequiresCookieInUrl("myCookieInUrl")
	Set<HttpMethod> getEventOptionsWithRequires(@Path long date);

	@Options("/events/{date}")
	@RequiresAuthentication
	Set<HttpMethod> getEventOptionsWithAuthentication(@Path long date);

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
