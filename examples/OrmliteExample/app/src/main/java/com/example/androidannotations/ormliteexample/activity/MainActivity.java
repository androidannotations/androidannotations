package com.example.androidannotations.ormliteexample.activity;

import android.app.Activity;
import android.widget.TextView;

import com.example.androidannotations.ormliteexample.R;
import com.example.androidannotations.ormliteexample.bean.Article;
import com.example.androidannotations.ormliteexample.bean.User;
import com.example.androidannotations.ormliteexample.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;

import java.util.Iterator;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @OrmLiteDao(helper = DatabaseHelper.class, model = User.class)
    Dao<User, Integer> userDao;

    @ViewById(R.id.tv)
    TextView tv;

    @AfterViews
    void show() {
        try {
            User user = userDao.queryForId(1);
            StringBuilder builder = new StringBuilder();
            builder.append("User name:" + user.name + "\n");
            Iterator<Article> iterator = user.articles.iterator();
            builder.append("Articles:");
            while (iterator.hasNext()) {
                Article article = iterator.next();
                builder.append(article.id + "." + article.title + " ");
            }
            tv.setText(builder.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
