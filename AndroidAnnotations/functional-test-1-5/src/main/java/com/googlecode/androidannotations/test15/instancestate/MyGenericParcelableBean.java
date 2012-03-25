package com.googlecode.androidannotations.test15.instancestate;

import android.os.Parcel;
import android.os.Parcelable;

public class MyGenericParcelableBean <T> implements Parcelable {

	private final T t;

	public MyGenericParcelableBean(T t) {
		this.t = t;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((t == null) ? 0 : t.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		MyGenericParcelableBean other = (MyGenericParcelableBean) obj;
		if (t == null) {
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		return true;
	}

}
