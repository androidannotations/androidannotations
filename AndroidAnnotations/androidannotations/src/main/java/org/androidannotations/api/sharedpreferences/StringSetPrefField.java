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

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import org.apache.pig.impl.util.ObjectSerializer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class StringSetPrefField extends AbstractPrefField {

	StringSetPrefField(SharedPreferences sharedPreferences, String key) {
		super(sharedPreferences, key);
	}

	public Set<String> get() {
		return getOr(new TreeSet<String>());
	}

	@SuppressWarnings("unchecked")
	public Set<String> getOr(Set<String> defaultValue) {
		Object obj;
		try {
			obj = ObjectSerializer.deserialize(sharedPreferences.getString(key, null));
		} catch (IOException e) {
			return null;
		}
		
		if(obj == null) {
			return defaultValue;
		}
		
		if(!(obj instanceof Set<?>)) {
			return defaultValue;
		}

		// No way to check further.
		
		return (Set<String>) obj;
	}

	public void put(Set<String> value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		setValue(editor, key, value);
		apply(editor);
	}

	static void setValue(Editor editor, String key, Set<String> value) {
		try {
			editor.putString(key, ObjectSerializer.serialize((Serializable) value));
		} catch (IOException e) {
			throw new RuntimeException("Argument for key `" + key + "` does not implement the Serializable interface");
		}
	}
}
