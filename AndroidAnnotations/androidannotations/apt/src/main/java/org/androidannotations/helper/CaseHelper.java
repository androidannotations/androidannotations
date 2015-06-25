/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CaseHelper {

	private static final Pattern PATTERN = Pattern.compile("([A-Z]|[a-z])[a-z0-9]*");

	private CaseHelper() {
	}

	public static String camelCaseToSnakeCase(String camelCase) {

		List<String> tokens = new ArrayList<>();
		Matcher matcher = PATTERN.matcher(camelCase);
		String acronym = "";
		while (matcher.find()) {
			String found = matcher.group();
			if (found.matches("^[A-Z]$")) {
				acronym += found;
			} else {
				if (acronym.length() > 0) {
					// we have an acronym to add before we continue
					tokens.add(acronym);
					acronym = "";
				}
				tokens.add(found.toLowerCase());
			}
		}
		if (acronym.length() > 0) {
			tokens.add(acronym);
		}
		if (tokens.size() > 0) {
			StringBuilder sb = new StringBuilder(tokens.remove(0));
			for (String s : tokens) {
				sb.append("_").append(s);
			}
			return sb.toString();
		} else {
			return camelCase;
		}
	}

	public static String camelCaseToUpperSnakeCase(String camelCase) {
		return camelCaseToSnakeCase(camelCase).toUpperCase();
	}

	public static String lowerCaseFirst(String string) {
		if (string.length() < 2) {
			return string.toLowerCase();
		}
		String first = string.substring(0, 1).toLowerCase();
		String end = string.substring(1, string.length());
		return first + end;
	}

	public static String camelCaseToUpperSnakeCase(String prefix, String camelCase, String suffix) {
		if (prefix != null && !camelCase.startsWith(prefix)) {
			camelCase = prefix + "_" + camelCase;
		}
		if (suffix != null && !camelCase.toLowerCase().endsWith(suffix.toLowerCase())) {
			camelCase = camelCase + "_" + suffix;
		}
		return camelCaseToUpperSnakeCase(camelCase);
	}

}
