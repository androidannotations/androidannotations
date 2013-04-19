package org.androidannotations.test15;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableSerializableData implements Parcelable, Serializable {

	private static final long serialVersionUID = 920532042616086169L;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	}
}
