package org.androidannotations.api.builder;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class FragmentBuilder<I extends FragmentBuilder<I>> extends Builder {

	protected Bundle args;

	public FragmentBuilder() {
		args = new Bundle();
	}

	public I putAll(Bundle map) {
		args.putAll(map);
		return (I) this;
	}

	public I putBoolean(String key, boolean value) {
		args.putBoolean(key, value);
		return (I) this;
	}

	public I putByte(String key, byte value) {
		args.putByte(key, value);
		return (I) this;
	}

	public I putChar(String key, char value) {
		args.putChar(key, value);
		return (I) this;
	}

	public I putShort(String key, short value) {
		args.putShort(key, value);
		return (I) this;
	}

	public I putInt(String key, int value) {
		args.putInt(key, value);
		return (I) this;
	}

	public I putLong(String key, long value) {
		args.putLong(key, value);
		return (I) this;
	}

	public I putFloat(String key, float value) {
		args.putFloat(key, value);
		return (I) this;
	}

	public I putDouble(String key, double value) {
		args.putDouble(key, value);
		return (I) this;
	}

	public I putString(String key, String value) {
		args.putString(key, value);
		return (I) this;
	}

	public I putCharSequence(String key, CharSequence value) {
		args.putCharSequence(key, value);
		return (I) this;
	}

	public I putParcelable(String key, Parcelable value) {
		args.putParcelable(key, value);
		return (I) this;
	}

	public I putParcelableArray(String key, Parcelable[] value) {
		args.putParcelableArray(key, value);
		return (I) this;
	}

	public I putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
		args.putParcelableArrayList(key, value);
		return (I) this;
	}

	public I putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
		args.putSparseParcelableArray(key, value);
		return (I) this;
	}

	public I putIntegerArrayList(String key, ArrayList<Integer> value) {
		args.putIntegerArrayList(key, value);
		return (I) this;
	}

	public I putStringArrayList(String key, ArrayList<String> value) {
		args.putStringArrayList(key, value);
		return (I) this;
	}

	public I putSerializable(String key, Serializable value) {
		args.putSerializable(key, value);
		return (I) this;
	}

	public I putBooleanArray(String key, boolean[] value) {
		args.putBooleanArray(key, value);
		return (I) this;
	}

	public I putByteArray(String key, byte[] value) {
		args.putByteArray(key, value);
		return (I) this;
	}

	public I putShortArray(String key, short[] value) {
		args.putShortArray(key, value);
		return (I) this;
	}

	public I putCharArray(String key, char[] value) {
		args.putCharArray(key, value);
		return (I) this;
	}

	public I putIntArray(String key, int[] value) {
		args.putIntArray(key, value);
		return (I) this;
	}

	public I putLongArray(String key, long[] value) {
		args.putLongArray(key, value);
		return (I) this;
	}

	public I putFloatArray(String key, float[] value) {
		args.putFloatArray(key, value);
		return (I) this;
	}

	public I putDoubleArray(String key, double[] value) {
		args.putDoubleArray(key, value);
		return (I) this;
	}

	public I putStringArray(String key, String[] value) {
		args.putStringArray(key, value);
		return (I) this;
	}

	public I putBundle(String key, Bundle value) {
		args.putBundle(key, value);
		return (I) this;
	}
}
