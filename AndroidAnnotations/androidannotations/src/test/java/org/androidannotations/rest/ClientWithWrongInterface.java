package org.androidannotations.rest;

import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(converters = { MappingJacksonHttpMessageConverter.class })
public interface ClientWithWrongInterface extends Comparable<String> {

}
