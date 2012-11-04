package com.googlecode.androidannotations.rest;

import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(converters = { AbstractConverter.class })
public interface ClientWithAbstractConverter {

}
