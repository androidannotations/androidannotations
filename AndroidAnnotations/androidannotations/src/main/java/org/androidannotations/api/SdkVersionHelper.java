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
package org.androidannotations.api;

import android.os.Build;
import android.os.Build.VERSION;

public class SdkVersionHelper {

	public static int getSdkInt() {
		if (Build.VERSION.RELEASE.startsWith("1.5"))
			return 3;

		return HelperInternal.getSdkIntInternal();
	}

	private static class HelperInternal {
		private static int getSdkIntInternal() {
			return VERSION.SDK_INT;
		}
	}

}
