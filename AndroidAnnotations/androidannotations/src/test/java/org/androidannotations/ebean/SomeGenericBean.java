package org.androidannotations.ebean;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class SomeGenericBean<T> {

    @Background
	void someMethod(List<? super T> list){

	}

    void someOtherMethod(List<? super T> list){

    }

}
