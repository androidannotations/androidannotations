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

public abstract class EditorHelper<T extends EditorHelper<T>> {

	private final Editor editor;

	public EditorHelper(SharedPreferences sharedPreferences) {
		editor = sharedPreferences.edit();
	}

	protected Editor getEditor() {
		return editor;
	}

	public final T clear() {
		editor.clear();
		return cast();
	}

	public final void apply() {
		SharedPreferencesCompat.apply(editor);
	}

	protected IntPrefEditorField<T> intField(String key) {
		return new IntPrefEditorField<>(cast(), key);
	}

	protected StringPrefEditorField<T> stringField(String key) {
		return new StringPrefEditorField<>(cast(), key);
	}

	protected StringSetPrefEditorField<T> stringSetField(String key) {
		return new StringSetPrefEditorField<>(cast(), key);
	}

	protected BooleanPrefEditorField<T> booleanField(String key) {
		return new BooleanPrefEditorField<>(cast(), key);
	}

	protected FloatPrefEditorField<T> floatField(String key) {
		return new FloatPrefEditorField<>(cast(), key);
	}

	protected LongPrefEditorField<T> longField(String key) {
		return new LongPrefEditorField<>(cast(), key);
	}

	@SuppressWarnings("unchecked")
	private T cast() {
		return (T) this;
	}

}
