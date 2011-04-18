package com.googlecode.androidannotations.test15.rest;

import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.googlecode.androidannotations.annotations.rest.Delete;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Head;
import com.googlecode.androidannotations.annotations.rest.Options;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Put;
import com.googlecode.androidannotations.annotations.rest.Rest;


@Rest("http://company.com/ajax/services") // if defined, the url will be added as a prefix to every request
public interface MyService {

  // url variables are mapped to method parameter names. 
  @Get("/events/{year}/{location}")
  EventList getEvents(String location, int year);

  // The response can be a ResponseEntity<T>
  @Get("/events/{year}/{location}")
  ResponseEntity<EventList> getEvents2(String location, int year) throws RestClientException; // You may (or may not) declare throwing RestClientException (as a reminder, since it's a RuntimeException), but nothing else.

  // There should be max 1 parameter that is not mapped to an attribute. This parameter will be used as the post entity.
  @Post("/events/{year}/{location}")
  EventList addEvent(String location, Event event, int year);

  @Put("/events/{year}/{location}")
  EventList updateEvent(String location, Event event, int year);

  // url variables are mapped to method parameter names. 
  @Delete("/events/{id}")
  void removeEvent(long id);

  @Head("/events/{year}/{location}")
  HttpHeaders getEventheaders(String location, int year);

  @Options("/events/{year}/{location}")
  Set<HttpMethod> getEventOptions(String location, int year);

  // if you need to add some configuration to the Spring RestTemplate.
  RestTemplate getRestTemplate();

}
