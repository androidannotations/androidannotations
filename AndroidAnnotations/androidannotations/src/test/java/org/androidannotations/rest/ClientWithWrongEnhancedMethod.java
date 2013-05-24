package org.androidannotations.rest;

import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Rest(converters = { MappingJacksonHttpMessageConverter.class })
public interface ClientWithWrongEnhancedMethod {
    // Correct
    RestTemplate getTemplate();
    String getRootUrl();

    // Wrong
    Object getRestTemplate();
    String getURL();
    String getRootURL();
    String getRootURL(String param);
    boolean setRootURL();
}