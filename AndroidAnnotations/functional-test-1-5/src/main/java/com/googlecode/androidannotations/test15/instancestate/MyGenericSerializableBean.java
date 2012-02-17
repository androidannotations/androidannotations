package com.googlecode.androidannotations.test15.instancestate;

import java.io.Serializable;

public class MyGenericSerializableBean<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final T t;

	public MyGenericSerializableBean(T t) {
		this.t = t;
	}

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

		@SuppressWarnings("unchecked")
		MyGenericSerializableBean<T> other = (MyGenericSerializableBean<T>) obj;
		if (t == null) {
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		return true;
	}


	
}
