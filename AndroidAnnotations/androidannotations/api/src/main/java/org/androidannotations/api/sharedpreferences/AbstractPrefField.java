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
import android.content.SharedPreferences.Editor;

public abstract class AbstractPrefField<T> {

	protected final T defaultValue;

	protected final SharedPreferences sharedPreferences;
	protected final String key;

	public AbstractPrefField(SharedPreferences sharedPreferences, String key, T defaultValue) {
		this.sharedPreferences = sharedPreferences;
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public final boolean exists() {
		return sharedPreferences.contains(key);
	}

	public String key() {
		return this.key;
	}

	public final T get() {
		return getOr(defaultValue);
	}

	public abstract T getOr(T defaultValue);

	public final void put(T value) {
		putInternal((value == null) ? defaultValue : value);
	};

	protected abstract void putInternal(T value);

	public final void remove() {
		apply(edit().remove(key));
	}

	protected Editor edit() {
		return sharedPreferences.edit();
	}

	protected final void apply(Editor editor) {
		SharedPreferencesCompat.apply(editor);
	}

}
