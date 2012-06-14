package com.googlecode.androidannotations.test15.ebean;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Beans;
import com.googlecode.androidannotations.annotations.EActivity;

import android.app.Activity;

@EActivity
public class CollectionInjectedActivity extends Activity {

	@Bean
	@Beans(value = { SomeItemImpl.class, SomeImplementation.class })
	public Collection<SomeInterface> collection;

	@Beans
	public Collection<SomeInterface> emptyCollection;

	@Bean(SomeComplexGenericList.class)
	@Beans(value = { SomeItemImpl.class, SomeImplementation.class })
	public List<SomeInterface> someListInterface;

	@Bean
	@Beans(value = { SomeItemImpl.class, SomeImplementation.class })
	public SomeComplexGenericList<?> someListImpl;

	@Bean(SomeSimpleList.class)
	@Beans(value = { SomeItemImpl.class, SomeImplementation.class })
	public SomeSimpleList someOtherList;

	@Beans
	public List<SomeInterface> giveMeList;

	@Beans
	public Set<SomeInterface> giveMeSet;
}