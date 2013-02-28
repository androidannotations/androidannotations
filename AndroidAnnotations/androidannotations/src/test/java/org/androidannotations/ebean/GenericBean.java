package org.androidannotations.ebean;

import java.util.Map;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;

@EBean
public class GenericBean<A extends Number, B extends Map<String, A>> {

	@RootContext
	Context context;

}
