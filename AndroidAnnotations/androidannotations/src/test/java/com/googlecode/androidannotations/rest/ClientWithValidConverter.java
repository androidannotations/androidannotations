package com.googlecode.androidannotations.rest;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(converters = { MappingJacksonHttpMessageConverter.class })
public interface ClientWithValidConverter {

}
