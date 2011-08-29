/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
package com.googlecode.androidannotations.api.sharedpreferences;

import android.content.SharedPreferences;

public final class FloatPrefField extends AbstractPrefField {

	private final float defaultValue;

	FloatPrefField(SharedPreferences sharedPreferences, String key, float defaultValue) {
		super(sharedPreferences, key);
		this.defaultValue = defaultValue;
	}

	public float get() {
		return get(defaultValue);
	}

	public float get(float defaultValue) {
		return sharedPreferences.getFloat(key, defaultValue);
	}

	public void put(float value) {
		apply(edit().putFloat(key, value));
	}

}
