/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.helper;

public class LogHelper {

    private static final int MAX_TAG_LEN = 23;

    private LogHelper() {
    }

    /*
     * Log tag length needs to be limited to 23.
     * http://developer.android.com/reference/android/util/Log.html#isLoggable(java.lang.String
     */
    public static String trimLogTagToSize(String tag) {
        if (tag == null) {
            return "";
        } else {
            return tag.substring(0, Math.min(MAX_TAG_LEN, tag.length()));
        }
    }

}
