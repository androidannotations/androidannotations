package com.googlecode.androidannotations.test15.efragment;

import android.support.v4.app.FragmentActivity;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.FragmentByTag;
import com.googlecode.androidannotations.test15.R;

@EActivity(R.layout.fragments)
public class MyFragmentActivity extends FragmentActivity {

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
	
	@Bean
	public BeanWithFragments beanWithFragments;

}
