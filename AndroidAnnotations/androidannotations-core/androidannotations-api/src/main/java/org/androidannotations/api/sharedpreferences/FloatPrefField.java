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
package org.androidannotations.api.sharedpreferences;

import android.content.SharedPreferences;

public final class FloatPrefField extends AbstractPrefField<Float> {

	FloatPrefField(SharedPreferences sharedPreferences, String key, Float defaultValue) {
		super(sharedPreferences, key, defaultValue);
	}

	@Override
	public Float getOr(Float defaultValue) {
		try {
			return sharedPreferences.getFloat(key, defaultValue);
		} catch (ClassCastException e) {
			// The pref could be a String, if that is the case try this
			// recovery bit
			try {
				String value = sharedPreferences.getString(key, "" + defaultValue);
				return Float.parseFloat(value);
			} catch (Exception e2) {
				// our recovery bit failed. The problem is elsewhere. Send the
				// original error
				throw e;
			}
		}

	}

	@Override
	protected void putInternal(Float value) {
		apply(edit().putFloat(key, value));
	}

}
