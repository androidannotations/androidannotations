package com.googlecode.androidannotations.rest;

import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(converters = { Object.class })
public interface ClientWithNonConverter {

}
