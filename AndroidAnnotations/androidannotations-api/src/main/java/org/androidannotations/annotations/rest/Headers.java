package org.androidannotations.annotations.rest;

/**
 * Use on methods in {@link Rest} annotated class to add headers to a given method
 */
public @interface Headers {
    Header[] value();
}
