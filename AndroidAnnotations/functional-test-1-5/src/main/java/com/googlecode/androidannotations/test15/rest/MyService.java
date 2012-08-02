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
package com.googlecode.androidannotations.test15.rest;

import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Delete;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Head;
import com.googlecode.androidannotations.annotations.rest.Options;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Put;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;

@Rest("http://company.com/ajax/services")
// if defined, the url will be added as a prefix to every request
public interface MyService {

	// url variables are mapped to method parameter names.
	@Get("/events/{year}/{location}")
	@Accept(MediaType.APPLICATION_JSON)
	EventList getEvents(String location, int year);

	@Get("/events/{year}/{location}")
	@Accept("application/json")
	Event[] getEventsArray(String location, int year);

	@Get("/events/{year}/{location}")
	@Accept(MediaType.APPLICATION_JSON)
	Event[][] getEventsArrayOfArrays(String location, int year);

	// The response can be a ResponseEntity<T>
	@Get("/events/{year}/{location}")
	/*
	 * You may (or may not) declare throwing RestClientException (as a reminder,
	 * since it's a RuntimeException), but nothing else.
	 */
	ResponseEntity<EventList> getEvents2(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	ResponseEntity<Event[]> getEventsArray2(String location, int year) throws RestClientException;

	@Get("/events/{year}/{location}")
	ResponseEntity<Event[][]> getEventsArrayOfArrays2(String location, int year) throws RestClientException;

	// There should be max 1 parameter that is not mapped to an attribute. This
	// parameter will be used as the post entity.
	@Post("/events/")
	@Accept(MediaType.APPLICATION_JSON)
	Event addEvent(Event event);

	@Post("/events/{year}/")
	Event addEvent(Event event, int year);

	@Post("/events/")
	ResponseEntity<Event> addEvent2(Event event);

	/**
	 * Output different then input
	 */
	@Post("/events/")
	ResponseEntity<String> addEvent3(Event event);

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

	@Put("/events/{id}")
	void updateEvent(Event event, int id);

	// url variables are mapped to method parameter names.
	@Delete("/events/{id}")
	void removeEvent(long id);

	@Head("/events/{year}/{location}")
	HttpHeaders getEventHeaders(String location, int year);

	@Options("/events/{year}/{location}")
	Set<HttpMethod> getEventOptions(String location, int year);

	// if you need to add some configuration to the Spring RestTemplate.
	RestTemplate getRestTemplate();

	void setRestTemplate(RestTemplate restTemplate);

	void setRootUrl(String test);
}
