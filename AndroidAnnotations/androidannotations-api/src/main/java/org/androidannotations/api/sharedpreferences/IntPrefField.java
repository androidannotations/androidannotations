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
package org.androidannotations.api.sharedpreferences;

import android.content.SharedPreferences;

public final class IntPrefField extends AbstractPrefField {

	private final int defaultValue;

	IntPrefField(SharedPreferences sharedPreferences, String key, int defaultValue) {
		super(sharedPreferences, key);
		this.defaultValue = defaultValue;
	}

	public int get() {
		return getOr(defaultValue);
	}

	public int getOr(int defaultValue) {
		return sharedPreferences.getInt(key, defaultValue);
	}

	public void put(int value) {
		apply(edit().putInt(key, value));
	}

}
