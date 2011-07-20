package com.googlecode.androidannotations.helper;

import java.util.Collections;
import java.util.List;

public class AndroidManifest {

	private final String applicationPackage;
	private final List<String> activityValidQualifiedNames;
    private final List<String> applicationClassValidNames;

	public AndroidManifest(String applicationPackage, List<String> applicationClassValidNames, List<String> activityValidQualifiedNames) {
		this.applicationPackage = applicationPackage;
        this.applicationClassValidNames = applicationClassValidNames;
		this.activityValidQualifiedNames = activityValidQualifiedNames;
	}

	public String getApplicationPackage() {
		return applicationPackage;
	}

	public List<String> getActivityValidQualifiedNames() {
		return Collections.unmodifiableList(activityValidQualifiedNames);
	}

}
