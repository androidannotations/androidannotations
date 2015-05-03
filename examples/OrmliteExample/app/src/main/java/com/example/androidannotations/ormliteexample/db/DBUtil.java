package com.example.androidannotations.ormliteexample.db;

import android.content.Context;

import com.example.androidannotations.ormliteexample.R;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.androidannotations.annotations.RootContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/5/1.
 */
@EBean(scope = Scope.Singleton)
public class DBUtil {
    @RootContext
    Context context;

    public void initDatabase() {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.test);
            File dbPath = context.getDatabasePath("");
            if (!dbPath.exists()) {
                dbPath.mkdirs();
            }
            File dbFile = new File(dbPath.getAbsolutePath() + "/test.db");
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(dbFile);
            byte[] buffer = new byte[1024];
            int hasRead = -1;
            while ((hasRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, hasRead);
            }
            fos.close();
            is.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
