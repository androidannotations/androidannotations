package com.example.androidannotations.ormliteexample.bean;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Administrator on 2015/5/1.
 */
public class Article {
    @DatabaseField(generatedId = true)
    public Integer id;
    @DatabaseField
    public String title;
    @DatabaseField(columnName = "user_id", foreign = true)
    public User user;

    /**
     * A no-argument constructor is necessary for Ormlite.
     */
    public Article() {
    }
}
