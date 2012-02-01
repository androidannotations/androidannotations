package com.googlecode.androidannotations.test15;

import android.os.Parcel;
import android.os.Parcelable;

public class MyParcelableBean implements Parcelable {

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}

}
