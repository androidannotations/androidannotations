package com.example.androidannotations.ormliteexample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Created by Administrator on 2015/5/1.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "test.db";
    public static final Integer DATABASE_VERSION = 1;

    /**
     * A single argument(Context) is required.
     * Because AndroidAnnotations use OpenHelperManager class.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        /**
         * Method1:
         * To create a new database, you can use getReadableDatabase(); or getWritableDatabase();
         */
        //getWritableDatabase();

        /**
         * Method2:
         * Create a database from existing test.db in the raw folder.
         */
        DBUtil_ dbUtil = DBUtil_.getInstance_(context);
        dbUtil.initDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        /**
         * Create new tables here.
         * TableUtils.createTable(connectionSource, User.class);
         * or
         * TableUtils.createTableIfNotExists(connectionSource, User.class);
         *
         */
        /*try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Article.class);
        } catch(Exception ex) {
            ex.printStackTrace();
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
