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
package org.androidannotations.test15.ormlite;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;

import org.androidannotations.test15.ormlite.User;
import org.androidannotations.test15.ormlite.UserDao;
import org.androidannotations.test15.ormlite.DatabaseHelper;

@EBean
public class OrmLiteBean {

	@OrmLiteDao(helper = DatabaseHelper.class, model = User.class)
	UserDao userDao;

}
