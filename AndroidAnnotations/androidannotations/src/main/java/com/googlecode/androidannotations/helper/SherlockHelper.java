/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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

import java.util.Arrays;
import java.util.List;

import com.googlecode.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JClass;

public class SherlockHelper {

	public static final List<String> SHERLOCK_ACTIVITIES_LIST_CLASS = Arrays.asList(new String[] {
			"com.actionbarsherlock.app.SherlockActivity",
			"com.actionbarsherlock.app.SherlockFragmentActivity",
			"com.actionbarsherlock.app.SherlockListActivity",
			"com.actionbarsherlock.app.SherlockPreferenceActivity"
			});
	
	public SherlockHelper() {
	}

	public boolean usesSherlock(EBeanHolder holder) {
		// Check whether the Activity extends one of the ActionBarSherlock Activity types
		for (String sherlockClass : SHERLOCK_ACTIVITIES_LIST_CLASS) {
			JClass activityClass = holder.eBean;
			while (activityClass != null) {
				if (activityClass.fullName().equals(sherlockClass)) {
					return true;
				}
				activityClass = activityClass._extends();
			}
		}
		return false;
	}
}
