package org.androidannotations.rest;

import org.androidannotations.annotations.rest.Header;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(converters = {MappingJacksonHttpMessageConverter.class})
public interface ClientWithOneHeader {

    @Header(key="testKey", value="testVal")
    void requestWithHeader();

}
