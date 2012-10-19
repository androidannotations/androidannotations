package com.googlecode.androidannotations.roboguiceexample;

import java.util.List;

import roboguice.application.RoboApplication;
import roboguice.config.AbstractAndroidModule;

import com.google.inject.Module;

public class AstroApplication extends RoboApplication {

	@Override
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new AstroModule());
	}

	static class AstroModule extends AbstractAndroidModule {
		@Override
		protected void configure() {
			bind(GreetingService.class).to(GreetingServiceToastImpl.class);
		}
	}

}
