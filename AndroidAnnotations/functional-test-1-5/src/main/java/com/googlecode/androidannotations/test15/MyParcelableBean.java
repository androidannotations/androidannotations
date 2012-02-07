package com.googlecode.androidannotations.test15;

import android.os.Parcel;
import android.os.Parcelable;

public class MyParcelableBean implements Parcelable {

	private final int	x;

	public MyParcelableBean(int x) {
		this.x = x;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(x);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyParcelableBean other = (MyParcelableBean) obj;
		if (x != other.x)
			return false;
		return true;
	}

}
