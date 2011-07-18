package com.googlecode.androidannotations.test15;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;

@EActivity
public class ExtraInjectedActivity extends Activity{
    
    public static class CustomData implements Serializable {

        private static final long serialVersionUID = 1L;
        
    }
    
    @Extra("stringExtra")
    String simpleExtra;
    
//    @Extra("arrayExtra")
//    CustomData[] arrayExtra;
//    
//    @Extra("listExtra")
//    List<String> listExtra;

}
