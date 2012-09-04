package com.googlecode.androidannotations.test15.efragment;

import android.app.Fragment;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.test15.ebean.SomeBean;

@EFragment
public abstract class AbstractFragment extends Fragment {
	
	@Bean
	SomeBean someBean;

}
