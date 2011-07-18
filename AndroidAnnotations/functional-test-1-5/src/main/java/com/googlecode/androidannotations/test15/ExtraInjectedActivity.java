package com.googlecode.androidannotations.test15;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;

@EActivity
public class ExtraInjectedActivity extends Activity{
    
    @Extra("stringExtra")
    String stringExtra;
    
    @Extra("arrayExtra")
    CustomData[] arrayExtra;
//    
//    @Extra("listExtra")
//    List<String> listExtra;

}
