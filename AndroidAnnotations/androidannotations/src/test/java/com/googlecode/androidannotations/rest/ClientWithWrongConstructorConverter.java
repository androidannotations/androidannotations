package com.googlecode.androidannotations.rest;

import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(converters = { WrongConstructorConverter.class })
public interface ClientWithWrongConstructorConverter {

}
