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
package org.androidannotations.rclass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManifestPackageExtractor {

	private static final Pattern pattern = Pattern.compile("package\\s*=\\s*\"([^\"]+)\"");
	private Matcher matcher;
	private boolean matches;

	public ManifestPackageExtractor(String manifestLine) {
		if (manifestLine != null) {
			matcher = pattern.matcher(manifestLine);
			matches = matcher.find();
		} else {
			matches = false;
		}
	}

	public boolean matches() {
		return matches;
	}

	public String extract() {
		return matches ? matcher.group(1) : null;
	}

}
