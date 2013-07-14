package org.androidannotations.rest;

import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.androidannotations.api.rest.RestClientHeaders;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(converters = { MappingJacksonHttpMessageConverter.class })
public interface ClientWithAllInterfaces extends RestClientErrorHandling, RestClientHeaders, RestClientRootUrl, RestClientSupport {

}
