/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.helper;

import java.util.Collections;
import java.util.List;

public class AndroidManifest {

    private final String applicationPackage;
    private final List<String> activityQualifiedNames;
    private final String applicationClassName;

    public AndroidManifest(String applicationPackage, String applicationClassName, List<String> activityQualifiedNames) {
        this.applicationPackage = applicationPackage;
        this.applicationClassName = applicationClassName;
        this.activityQualifiedNames = activityQualifiedNames;
    }

    public String getApplicationPackage() {
        return applicationPackage;
    }

    public List<String> getActivityQualifiedNames() {
        return Collections.unmodifiableList(activityQualifiedNames);
    }

    public String getApplicationClassName() {
        return applicationClassName;
    }

}
