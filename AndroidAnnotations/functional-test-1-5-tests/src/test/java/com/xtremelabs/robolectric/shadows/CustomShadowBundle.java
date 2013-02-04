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
package com.xtremelabs.robolectric.shadows;

import static com.xtremelabs.robolectric.Robolectric.shadowOf_;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

@SuppressWarnings("unchecked")
@Implements(Bundle.class)
public class CustomShadowBundle {

	Map<String, Object>	mMap;

	public CustomShadowBundle() {
		mMap = new HashMap<String, Object>();
	}

	@Implementation
	public boolean isEmpty() {
		return mMap.isEmpty();
	}

	@Implementation
	public void clear() {
		mMap.clear();
	}

	@Implementation
	public boolean containsKey(String key) {
		return mMap.containsKey(key);
	}

	@Implementation
	public Object get(String key) {
		return mMap.get(key);
	}

	@Implementation
	public void remove(String key) {
		mMap.remove(key);
	}

	@Implementation
	public Set<String> keySet() {
		return mMap.keySet();
	}

	@Implementation
	public void putBoolean(String key, boolean value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putByte(String key, byte value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putChar(String key, char value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putShort(String key, short value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putInt(String key, int value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putLong(String key, long value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putFloat(String key, float value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putDouble(String key, double value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putString(String key, String value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putCharSequence(String key, CharSequence value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putParcelable(String key, Parcelable value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putParcelableArray(String key, Parcelable[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putIntegerArrayList(String key, ArrayList<Integer> value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putStringArrayList(String key, ArrayList<String> value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putSerializable(String key, Serializable value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putBooleanArray(String key, boolean[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putByteArray(String key, byte[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putShortArray(String key, short[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putCharArray(String key, char[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putIntArray(String key, int[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putLongArray(String key, long[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putFloatArray(String key, float[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putDoubleArray(String key, double[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putStringArray(String key, String[] value) {
		mMap.put(key, value);
	}

	@Implementation
	public void putBundle(String key, Bundle value) {
		mMap.put(key, value);
	}

	@Implementation
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	@Implementation
	public boolean getBoolean(String key, boolean defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Boolean) o;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	@Implementation
	public byte getByte(String key) {
		return getByte(key, (byte) 0);
	}

	@Implementation
	public Byte getByte(String key, byte defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Byte) o;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	@Implementation
	public char getChar(String key) {
		return getChar(key, (char) 0);
	}

	@Implementation
	public char getChar(String key, char defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Character) o;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	@Implementation
	public short getShort(String key) {
		return getShort(key, (short) 0);
	}

	@Implementation
	public short getShort(String key, short defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Short) o;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	@Implementation
	public int getInt(String key) {
		return getInt(key, 0);
	}

	@Implementation
	public int getInt(String key, int defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Integer) o;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	@Implementation
	public long getLong(String key) {
		return getLong(key, 0L);
	}

	@Implementation
	public long getLong(String key, long defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Long) o;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	@Implementation
	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}

	@Implementation
	public float getFloat(String key, float defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Float) o;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	@Implementation
	public double getDouble(String key) {
		return getDouble(key, 0.0);
	}

	@Implementation
	public double getDouble(String key, double defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return (Double) o;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	@Implementation
	public String getString(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (String) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public CharSequence getCharSequence(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (CharSequence) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public Bundle getBundle(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (Bundle) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public <T extends Parcelable> T getParcelable(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (T) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public Parcelable[] getParcelableArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (Parcelable[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (ArrayList<T>) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (SparseArray<T>) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public Serializable getSerializable(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (Serializable) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public ArrayList<Integer> getIntegerArrayList(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (ArrayList<Integer>) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public ArrayList<String> getStringArrayList(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (ArrayList<String>) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public boolean[] getBooleanArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (boolean[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public byte[] getByteArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (byte[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public short[] getShortArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (short[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public char[] getCharArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (char[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public int[] getIntArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (int[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public long[] getLongArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (long[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public float[] getFloatArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (float[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public double[] getDoubleArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (double[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Implementation
	public String[] getStringArray(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return (String[]) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mMap == null) ? 0 : mMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		obj = shadowOf_(obj);
		if (getClass() != obj.getClass())
			return false;
		CustomShadowBundle other = (CustomShadowBundle) obj;
		if (mMap == null) {
			if (other.mMap != null)
				return false;
		} else if (!mMap.equals(other.mMap))
			return false;
		return true;
	}


	
	
}
