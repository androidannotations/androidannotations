package com.googlecode.androidannotations.processing.rest;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

public class RestImplementationsHolder {

    private Map<Element, RestImplentationHolder> holders = new HashMap<Element, RestImplentationHolder>();

    public RestImplentationHolder create(Element element) {
    	RestImplentationHolder holder = new RestImplentationHolder();
        holders.put(element, holder);
        return holder;
    }

    public RestImplentationHolder getEnclosingHolder(Element enclosedElement) {
        Element activityElement = enclosedElement.getEnclosingElement();
        return holders.get(activityElement);
    }

    public RestImplentationHolder getRelativeHolder(Element element) {
        return holders.get(element);
    }

}
