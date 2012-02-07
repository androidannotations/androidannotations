package com.googlecode.androidannotations.test15;

import java.io.Serializable;

public class MySerializableBean implements Serializable {

	private static final long serialVersionUID = 398309810982L;

	private final int	x;

	public MySerializableBean(int x) {
		this.x = x;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MySerializableBean other = (MySerializableBean) obj;
		if (x != other.x)
			return false;
		return true;
	}

}
