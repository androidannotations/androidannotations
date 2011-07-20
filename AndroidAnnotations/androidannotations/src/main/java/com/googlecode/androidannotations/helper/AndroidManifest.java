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
