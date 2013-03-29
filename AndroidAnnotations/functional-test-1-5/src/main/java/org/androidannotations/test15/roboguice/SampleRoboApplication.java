/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test15.roboguice;

import java.util.List;

import roboguice.application.RoboApplication;

import com.google.inject.Module;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.test15.ebean.EmptyDependency;
import org.androidannotations.test15.ormlite.DatabaseHelper;
import org.androidannotations.test15.ormlite.User;
import org.androidannotations.test15.ormlite.UserDao;

@EApplication
public class SampleRoboApplication extends RoboApplication {
	
	@Bean
	public EmptyDependency someDependency;

	@OrmLiteDao(helper = DatabaseHelper.class, model = User.class)
	UserDao userDao;

	private Module module = new RobolectricSampleModule();

	@Override
	protected void addApplicationModules(List<Module> modules) {
		modules.add(module);
	}

	public void setModule(Module module) {
		this.module = module;
	}
}
