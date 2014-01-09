package org.androidannotations.ebean;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.List;

@EBean
public class SomeGenericBean<T> {

    @Background
	void someMethod(List<? super T> list){
	}

    void someOtherMethod(List<? super T> list){
    }

    @Background
    <N extends T> void someParameterizedMethod(List<? super N> lst, List<? extends N> lst2) {
    }

    @UiThread
    <T, S extends Number> void emptyUiMethod(List<? extends T> param, List<? super S> param2) {
    }


}
