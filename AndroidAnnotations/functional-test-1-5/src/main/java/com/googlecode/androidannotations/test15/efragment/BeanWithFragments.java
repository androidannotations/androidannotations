package com.googlecode.androidannotations.test15.efragment;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.FragmentByTag;
import com.googlecode.androidannotations.test15.R;

@EBean
public class BeanWithFragments {

	@FragmentById
	public MyFragment myFragment;
	
	@FragmentById
	public MySupportFragment mySupportFragment;

	@FragmentById(R.id.myFragment)
	public MyFragment myFragment2;

	@FragmentById(R.id.mySupportFragment)
	public MySupportFragment mySupportFragment2;
	
	@FragmentByTag
	public MyFragment myFragmentTag;
	
	@FragmentByTag
	public MySupportFragment mySupportFragmentTag;
	
	@FragmentByTag("myFragmentTag")
	public MyFragment myFragmentTag2;
	
	@FragmentByTag("mySupportFragmentTag")
	public MySupportFragment mySupportFragmentTag2;
	
	
	
}
