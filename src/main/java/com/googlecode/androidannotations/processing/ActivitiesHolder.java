package com.googlecode.androidannotations.processing;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

public class ActivitiesHolder {

    private Map<Element, ActivityHolder> activityHolders = new HashMap<Element, ActivityHolder>();

    public ActivityHolder create(Element activityElement) {
        ActivityHolder activityHolder = new ActivityHolder();
        activityHolders.put(activityElement, activityHolder);
        return activityHolder;
    }

    public ActivityHolder getEnclosingActivityHolder(Element enclosedElement) {
        Element activityElement = enclosedElement.getEnclosingElement();
        return activityHolders.get(activityElement);
    }

    public ActivityHolder getRelativeActivityHolder(Element element) {
        return activityHolders.get(element);
    }

}
