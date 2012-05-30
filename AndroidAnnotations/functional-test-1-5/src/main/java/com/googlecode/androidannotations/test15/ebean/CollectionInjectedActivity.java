package com.googlecode.androidannotations.test15.ebean;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;

import android.app.Activity;

@EActivity
public class CollectionInjectedActivity extends Activity {

	@Bean(items = { SomeImplementation.class, SomeImplementation.class })
	public Collection<SomeInterface> collection;

	@Bean
	public Collection<SomeInterface> emptyCollection;

	@Bean(value = SomeList.class, items = { SomeItemImpl.class })
	public Collection<SomeInterface> someList;

	@Bean
	public List<SomeInterface> giveMeList;

	@Bean
	public Set<SomeInterface> giveMeSet;
}