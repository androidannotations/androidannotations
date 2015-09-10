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
package org.androidannotations.api.bundle;

import java.lang.reflect.Array;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * Utility class for working with {@link Bundle} objects.
 */
public final class BundleHelper {

	private BundleHelper() {

	}

	/**
	 * This method extracts a {@link Parcelable} array from the {@link Bundle},
	 * and returns it in an array whose type is the exact {@link Parcelable}
	 * subclass. This is needed because {@link Bundle#getParcelable(String)}
	 * returns an array of {@link Parcelable}, and we would get
	 * {@link ClassCastException} when we assign it to {@link Parcelable}
	 * subclass arrays.
	 * 
	 * For more info, see <a
	 * href="https://github.com/excilys/androidannotations/issues/1208">this</a>
	 * url.
	 * 
	 * @param bundle
	 *            the bundle holding the array which is extracted
	 * @param key
	 *            the array is associated with this key
	 * @param type
	 *            the desired type of the returned array
	 * @param <T>
	 *            the element type of the returned array
	 * @return a {@link Parcelable} subclass typed array which holds the objects
	 *         from {@link Bundle#getParcelableArray(String)} or
	 *         <code>null</code> if {@link Bundle#getParcelableArray(String)}
	 *         returned <code>null</code> for the key
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Parcelable> T[] getParcelableArray(Bundle bundle, String key, Class<T[]> type) {
		Parcelable[] value = bundle.getParcelableArray(key);
		if (value == null) {
			return null;
		}
		Object copy = Array.newInstance(type.getComponentType(), value.length);
		System.arraycopy(value, 0, copy, 0, value.length);
		return (T[]) copy;
	}
}
