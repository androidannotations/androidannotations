package com.googlecode.androidannotations.test15.ormlite;

import com.j256.ormlite.dao.BaseDaoImpl;
import java.sql.SQLException;

public class UserDaoImpl extends BaseDaoImpl<User, Long> implements UserDao {

	protected UserDaoImpl(Class<User> dataClass) throws SQLException {
		super(dataClass);
	}
}