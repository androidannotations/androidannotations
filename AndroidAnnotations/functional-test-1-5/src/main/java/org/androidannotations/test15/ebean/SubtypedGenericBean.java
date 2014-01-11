package org.androidannotations.test15.ebean;

import com.squareup.otto.Produce;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.List;

@EBean
public class SubtypedGenericBean<S extends List<T>, T extends Number> {

	@Background
	void backgroundMethod(T param, S param2) {

	}

	@Produce
	public T genericMethod() {
		return null;
	}

	@UiThread
	<S extends List<T>> void uiMethod(S method) {

	}

}