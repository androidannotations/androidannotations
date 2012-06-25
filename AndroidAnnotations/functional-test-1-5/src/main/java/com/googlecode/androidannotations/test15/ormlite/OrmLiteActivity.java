/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.test15.ormlite;

import android.app.Activity;

import com.j256.ormlite.dao.Dao;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OrmLiteDao;

import com.googlecode.androidannotations.test15.ormlite.Car;
import com.googlecode.androidannotations.test15.ormlite.User;
import com.googlecode.androidannotations.test15.ormlite.UserDao;
import com.googlecode.androidannotations.test15.ormlite.OrmLiteBean;
import com.googlecode.androidannotations.test15.ormlite.DatabaseHelper;

@EActivity
public class OrmLiteActivity extends Activity {

	@OrmLiteDao(helper = DatabaseHelper.class, model = User.class)
	protected UserDao userDao;

	@OrmLiteDao(helper = DatabaseHelper.class, model = Car.class)
	protected Dao<Car, Long> carDao;

	@Bean
	protected OrmLiteBean ormLiteBean;
}
