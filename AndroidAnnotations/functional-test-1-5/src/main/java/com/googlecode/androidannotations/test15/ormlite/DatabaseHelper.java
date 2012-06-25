package com.googlecode.androidannotations.test15.ormlite;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.googlecode.androidannotations.test15.R;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "aaormlite.db";
	private static final int DATABASE_VERSION = 2;


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Car.class);
			TableUtils.createTable(connectionSource, User.class);
		} catch (SQLException e) {
			Log.e("aa-ormlite", "Unable to create database", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
	
	}
}