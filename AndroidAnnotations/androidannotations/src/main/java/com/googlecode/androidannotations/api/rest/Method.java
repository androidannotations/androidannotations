package com.googlecode.androidannotations.api.rest;

public enum Method {

	GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), //
	OPTIONS("OPTIONS"), HEAD("HEAD");
	
	private String value;
	
	Method (String value) {
		this.value = value;
	}

	public String getValue() {
    	return value;
    }
	
}
