/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.test.instancestate;

import android.os.Parcel;
import android.os.Parcelable;

public class MyParcelableBean implements Parcelable {

	private final int x;

	public MyParcelableBean(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	protected MyParcelableBean(Parcel in) {
		x = in.readInt();
	}

	public static final Creator<MyParcelableBean> CREATOR = new Creator<MyParcelableBean>() {
		@Override
		public MyParcelableBean createFromParcel(Parcel in) {
			return new MyParcelableBean(in);
		}

		@Override
		public MyParcelableBean[] newArray(int size) {
			return new MyParcelableBean[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(x);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MyParcelableBean other = (MyParcelableBean) obj;
		if (x != other.x) {
			return false;
		}
		return true;
	}

}
