package com.googlecode.androidannotations.roboguiceexample;

import java.util.List;

import roboguice.application.RoboApplication;

import com.google.inject.Module;

public class AstroApplication extends RoboApplication {

	@Override
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new AstroModule());
	}

}
