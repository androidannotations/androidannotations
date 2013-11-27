package org.androidannotations.ebean;

import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class SomeGenericBean<T> {

	void someMethod(List<? super T> list){

	}

}
