package org.androidannotations.ebean;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;

@EBean
public class GenericBean<A extends Object, B extends Number> {

	@RootContext
	Context context;

}
