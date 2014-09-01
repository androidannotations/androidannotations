package org.androidannotations.roboguiceexample;

import roboguice.config.AbstractAndroidModule;

public class AstroModule extends AbstractAndroidModule {
	@Override
	protected void configure() {
		bind(GreetingService.class).to(GreetingServiceToastImpl.class);
	}
}