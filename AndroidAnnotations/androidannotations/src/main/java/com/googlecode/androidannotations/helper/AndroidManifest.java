package com.googlecode.androidannotations.helper;

import java.util.Collections;
import java.util.List;

public class AndroidManifest {

	private final String applicationPackage;
	private final List<String> activityQualifiedNames;

	public AndroidManifest(String applicationPackage, List<String> activityQualifiedNames) {
		this.applicationPackage = applicationPackage;
		this.activityQualifiedNames = activityQualifiedNames;
	}

	public String getApplicationPackage() {
		return applicationPackage;
	}

	public List<String> getActivityQualifiedNames() {
		return Collections.unmodifiableList(activityQualifiedNames);
	}

}
