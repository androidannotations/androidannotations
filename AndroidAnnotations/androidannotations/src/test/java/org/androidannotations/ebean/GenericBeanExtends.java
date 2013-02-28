package org.androidannotations.ebean;

import java.util.ArrayList;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;

@EBean
public class GenericBeanExtends<A extends Object, B extends Number> extends ArrayList<A> {

	private static final long serialVersionUID = 1L;

	@RootContext
	Context context;

}
