package com.example.androidannotations.ormliteexample.bean;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

/**
 * Created by Administrator on 2015/5/1.
 */
public class User {
    @DatabaseField(generatedId = true)
    public Integer id;
    @DatabaseField
    public String name;
    @ForeignCollectionField(eager = true)
    public ForeignCollection<Article> articles;

    /**
     * A no-argument constructor is necessary for Ormlite.
     */
    public User() {
    }
}
