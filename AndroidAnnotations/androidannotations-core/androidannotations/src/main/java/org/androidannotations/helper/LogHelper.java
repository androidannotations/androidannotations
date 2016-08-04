/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import org.androidannotations.holder.GeneratedClassHolder;

public final class LogHelper {

	private static final int MAX_TAG_LEN = 23;

	private LogHelper() {
	}

	/**
	 * Log tag length needs to be limited to 23.
	 * 
	 * @see <a href=
	 *      "http://developer.android.com/reference/android/util/Log.html#isLoggable(java.lang.String,%20int)">
	 *      Log.isLoggable() JavaDoc</a>
	 */
	public static String trimLogTag(String tag) {
		if (tag == null) {
			return "";
		} else if (tag.length() > MAX_TAG_LEN) {
			return tag.substring(0, MAX_TAG_LEN);
		} else {
			return tag;
		}
	}

	/**
	 * Log tag length needs to be limited to 23.
	 *
	 * @see <a href=
	 *      "http://developer.android.com/reference/android/util/Log.html#isLoggable(java.lang.String,%20int)">
	 *      Log.isLoggable() JavaDoc</a>
	 */
	public static String logTagForClassHolder(GeneratedClassHolder holder) {
		return trimLogTag(holder.getGeneratedClass().name());
	}
}
