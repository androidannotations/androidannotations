package org.androidannotations.api.builder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class IntentBuilder<I extends IntentBuilder<I>> extends Builder {

	protected final Context context;
	protected final Intent intent;

	public IntentBuilder(Context context, Class<?> clazz) {
		this.context = context;
		intent = new Intent(context, clazz);
	}

	public IntentBuilder(Context context, Intent intent) {
		this.context = context;
		this.intent = intent;
	}

	public Context getContext() {
		return context;
	}

	public Intent get() {
		return intent;
	}

	public I flags(int flags) {
		intent.setFlags(flags);
		return (I) this;
	}

	public I extras(Bundle bundle) {
		intent.putExtras(bundle);
		return (I) this;
	}

	public I putExtra(String name, boolean value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, byte value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, char value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, short value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, int value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, long value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, float value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, double value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, String value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, CharSequence value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, Parcelable value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, Parcelable[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
		intent.putParcelableArrayListExtra(name, value);
		return (I) this;
	}

	public I putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
		intent.putIntegerArrayListExtra(name, value);
		return (I) this;
	}

	public I putStringArrayListExtra(String name, ArrayList<String> value) {
		intent.putStringArrayListExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, Serializable value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, boolean[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, byte[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, short[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, char[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, int[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, long[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, float[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, double[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, String[] value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtra(String name, Bundle value) {
		intent.putExtra(name, value);
		return (I) this;
	}

	public I putExtras(Intent src) {
		intent.putExtras(src);
		return (I) this;
	}
}
