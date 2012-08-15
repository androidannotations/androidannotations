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

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import java.sql.SQLException;

public class UserDaoImpl extends BaseDaoImpl<User, Long> implements UserDao {
	
	public UserDaoImpl(ConnectionSource connectionSource, Class<User> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}
	
	public UserDaoImpl(ConnectionSource connectionSource, DatabaseTableConfig<User> tableConfig) throws SQLException {
		super(connectionSource, tableConfig);
	}
}