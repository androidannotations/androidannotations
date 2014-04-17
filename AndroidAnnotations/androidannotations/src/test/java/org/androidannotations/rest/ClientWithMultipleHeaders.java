package org.androidannotations.rest;

import org.androidannotations.annotations.rest.Header;
import org.androidannotations.annotations.rest.Headers;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(converters = MappingJacksonHttpMessageConverter.class)
public interface ClientWithMultipleHeaders {

    @Headers({
            @Header(headerName="testKey", value="testVal"),
            @Header(headerName="testKey1", value="testVal1")
    })
    @Post("/test/")
    void requestWithOneHeader();
}
