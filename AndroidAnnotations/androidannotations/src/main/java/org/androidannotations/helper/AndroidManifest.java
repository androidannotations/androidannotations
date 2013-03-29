/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.helper;

import java.util.Collections;
import java.util.List;

public class AndroidManifest {

	private final String applicationPackage;
	private final List<String> componentQualifiedNames;
	private final List<String> permissionQualifiedNames;
	private final String applicationClassName;
	private final boolean libraryProject;
	private final boolean debugabble;

	public static AndroidManifest createManifest(String applicationPackage, String applicationClassName, List<String> componentQualifiedNames, List<String> permissionQualifiedNames, boolean debugabble) {
		return new AndroidManifest(false, applicationPackage, applicationClassName, componentQualifiedNames, permissionQualifiedNames, debugabble);
	}

	public static AndroidManifest createLibraryManifest(String applicationPackage) {
		return new AndroidManifest(true, applicationPackage, "", Collections.<String> emptyList(), Collections.<String> emptyList(), false);
	}

	private AndroidManifest(boolean libraryProject, String applicationPackage, String applicationClassName, List<String> componentQualifiedNames, List<String> permissionQualifiedNames, boolean debuggable) {
		this.libraryProject = libraryProject;
		this.applicationPackage = applicationPackage;
		this.applicationClassName = applicationClassName;
		this.componentQualifiedNames = componentQualifiedNames;
		this.permissionQualifiedNames = permissionQualifiedNames;
		this.debugabble = debuggable;
	}

	public String getApplicationPackage() {
		return applicationPackage;
	}

	public List<String> getComponentQualifiedNames() {
		return Collections.unmodifiableList(componentQualifiedNames);
	}

	public List<String> getPermissionQualifiedNames() {
		return Collections.unmodifiableList(permissionQualifiedNames);
	}

	public String getApplicationClassName() {
		return applicationClassName;
	}

	public boolean isLibraryProject() {
		return libraryProject;
	}

	public boolean isDebuggable() {
		return debugabble;
	}

}
