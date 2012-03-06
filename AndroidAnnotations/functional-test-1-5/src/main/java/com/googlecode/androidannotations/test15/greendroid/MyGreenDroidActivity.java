package com.googlecode.androidannotations.test15.greendroid;

import greendroid.app.GDActivity;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.test15.R;

@EActivity(R.layout.main)
public class MyGreenDroidActivity extends GDActivity {
	
	public int layoutResID;
	public boolean afterViewsCalled;

	@Override
	public void setActionBarContentView(int layoutResID) {
		this.layoutResID = layoutResID;
		super.setActionBarContentView(layoutResID);
	}
	
	@AfterViews
	void afterViews() {
		afterViewsCalled = true;
	}
	
}
