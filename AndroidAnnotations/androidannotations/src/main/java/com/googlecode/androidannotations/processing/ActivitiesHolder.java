/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
