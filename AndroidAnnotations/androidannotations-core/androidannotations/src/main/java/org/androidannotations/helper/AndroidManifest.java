/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AndroidManifest {

	private final String applicationPackage;
	private final List<String> componentQualifiedNames;
	private final Map<String, MetaDataInfo> metaDataQualifiedNames;
	private final List<String> permissionQualifiedNames;
	private final String applicationClassName;
	private final boolean libraryProject;
	private final boolean debugabble;
	private final int minSdkVersion;
	private final int maxSdkVersion;
	private final int targetSdkVersion;

	@Override
	public String toString() {
		return "AndroidManifest [applicationPackage=" + applicationPackage + ", componentQualifiedNames=" + componentQualifiedNames + ", metaDataQualifiedNames=" + metaDataQualifiedNames
				+ ", permissionQualifiedNames=" + permissionQualifiedNames + ", applicationClassName=" + applicationClassName + ", libraryProject=" + libraryProject + ", debugabble=" + debugabble
				+ ", minSdkVersion=" + minSdkVersion + ", maxSdkVersion=" + maxSdkVersion + ", targetSdkVersion=" + targetSdkVersion + "]";
	}

	// CHECKSTYLE:OFF

	public static AndroidManifest createManifest(String applicationPackage, String applicationClassName, List<String> componentQualifiedNames, Map<String, MetaDataInfo> metaDataQualifiedNames,
			List<String> permissionQualifiedNames, int minSdkVersion, int maxSdkVersion, int targetSdkVersion, boolean debugabble) {
		return new AndroidManifest(false, applicationPackage, applicationClassName, componentQualifiedNames, metaDataQualifiedNames, permissionQualifiedNames, minSdkVersion, maxSdkVersion,
				targetSdkVersion, debugabble);
	}

	public static AndroidManifest createLibraryManifest(String applicationPackage, int minSdkVersion, int maxSdkVersion, int targetSdkVersion) {
		return new AndroidManifest(true, applicationPackage, "", Collections.<String> emptyList(), new HashMap<String, MetaDataInfo>(), Collections.<String> emptyList(), minSdkVersion, maxSdkVersion,
				targetSdkVersion, false);
	}

	private AndroidManifest(boolean libraryProject, String applicationPackage, String applicationClassName, List<String> componentQualifiedNames, Map<String, MetaDataInfo> metaDataQualifiedNames,
			List<String> permissionQualifiedNames, int minSdkVersion, int maxSdkVersion, int targetSdkVersion, boolean debuggable) {
		this.libraryProject = libraryProject;
		this.applicationPackage = applicationPackage;
		this.applicationClassName = applicationClassName;
		this.componentQualifiedNames = componentQualifiedNames;
		this.metaDataQualifiedNames = metaDataQualifiedNames;
		this.permissionQualifiedNames = permissionQualifiedNames;
		this.minSdkVersion = minSdkVersion;
		this.maxSdkVersion = maxSdkVersion;
		this.targetSdkVersion = targetSdkVersion;
		this.debugabble = debuggable;
	}

	// CHECKSTYLE:ON

	public String getApplicationPackage() {
		return applicationPackage;
	}

	public List<String> getComponentQualifiedNames() {
		return Collections.unmodifiableList(componentQualifiedNames);
	}

	public Map<String, MetaDataInfo> getMetaDataQualifiedNames() {
		return Collections.unmodifiableMap(metaDataQualifiedNames);
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

	public int getMinSdkVersion() {
		return minSdkVersion;
	}

	public int getMaxSdkVersion() {
		return maxSdkVersion;
	}

	public int getTargetSdkVersion() {
		return targetSdkVersion;
	}

	public static final class MetaDataInfo {

		private final String name;
		private final String value;
		private final String resource;

		public MetaDataInfo(String name, String value, String resource) {
			this.name = name;
			this.value = value;
			this.resource = resource;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public String getResource() {
			return resource;
		}

		@Override
		public String toString() {
			return "{" + "name='" + name + '\'' + ", value='" + value + '\'' + ", resource='" + resource + '\'' + '}';
		}
	}
}
