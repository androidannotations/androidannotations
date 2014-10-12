package org.androidannotations.roboguiceexample;

import com.google.inject.AbstractModule;

public class AstroModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(GreetingService.class).to(GreetingServiceToastImpl.class);
	}
}