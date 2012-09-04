/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
	private final List<String> componentQualifiedNames;
	private final String applicationClassName;
	private final boolean libraryProject;

	public static AndroidManifest createManifest(String applicationPackage, String applicationClassName, List<String> componentQualifiedNames) {
		return new AndroidManifest(false, applicationPackage, applicationClassName, componentQualifiedNames);
	}

	public static AndroidManifest createLibraryManifest(String applicationPackage) {
		return new AndroidManifest(true, applicationPackage, "", Collections.<String> emptyList());
	}

	private AndroidManifest(boolean libraryProject, String applicationPackage, String applicationClassName, List<String> componentQualifiedNames) {
		this.libraryProject = libraryProject;
		this.applicationPackage = applicationPackage;
		this.applicationClassName = applicationClassName;
		this.componentQualifiedNames = componentQualifiedNames;
	}

	public String getApplicationPackage() {
		return applicationPackage;
	}

	public List<String> getComponentQualifiedNames() {
		return Collections.unmodifiableList(componentQualifiedNames);
	}

	public String getApplicationClassName() {
		return applicationClassName;
	}

	public boolean isLibraryProject() {
		return libraryProject;
	}

}
