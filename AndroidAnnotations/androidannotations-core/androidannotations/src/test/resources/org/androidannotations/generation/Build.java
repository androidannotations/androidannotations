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
package android.os;

/**
 * We have to put this on resources folder because we want to add it to
 * classpath only on some unit tests methods
 */
@SuppressWarnings("checkstyle:typename")
public class Build {

	public static class VERSION {
		public static final int SDK_INT = 20;
	}

	public static class VERSION_CODES {
		public static final int LOLLIPOP = 21;
		public static final int M = 23;
	}

}
