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