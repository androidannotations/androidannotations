package com.googlecode.androidannotations.test15;

import java.util.List;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SaveOnActivityDestroy;

@EActivity(R.layout.main)
public class SaveInstanceStateActivity extends Activity {

	@SaveOnActivityDestroy
	boolean myBoolean;

	@SaveOnActivityDestroy
	boolean [] myBooleanArray;

	@SaveOnActivityDestroy
	Boolean myBooleanObject;

	@SaveOnActivityDestroy
	Boolean[] myBooleanObjectArray;

	@SaveOnActivityDestroy
	byte myByte;

	@SaveOnActivityDestroy
	byte [] myByteArray;

	@SaveOnActivityDestroy
	Byte myByteObject;

	@SaveOnActivityDestroy
	Byte [] myByteObjectArray;

	@SaveOnActivityDestroy
	char myChar;

	@SaveOnActivityDestroy
	char [] myCharacterArray;

	@SaveOnActivityDestroy
	Character myCharacterObject;

	@SaveOnActivityDestroy
	Character [] myCharacterObjectArray;

	@SaveOnActivityDestroy
	double myDouble;

	@SaveOnActivityDestroy
	double [] myDoubleArray;

	@SaveOnActivityDestroy
	Double myDoubleObject;

	@SaveOnActivityDestroy
	Double [] myDoubleObjectArray;

	@SaveOnActivityDestroy
	float myFloat;

	@SaveOnActivityDestroy
	float [] myFloatArray;

	@SaveOnActivityDestroy
	Float myFloatObject;

	@SaveOnActivityDestroy
	Float [] myFloatObjectArray;

    @SaveOnActivityDestroy
    int myInt;

    @SaveOnActivityDestroy
    int [] myIntegerArray;

    @SaveOnActivityDestroy
    Integer myIntegerObject;

    @SaveOnActivityDestroy
    Integer [] myIntegerObjectArray;

    @SaveOnActivityDestroy
    long myLong;

    @SaveOnActivityDestroy
    long [] myLongArray;

    @SaveOnActivityDestroy
    Long myLongObject;

    @SaveOnActivityDestroy
    Long [] myLongObjectArray;

    @SaveOnActivityDestroy
    short myShort;

    @SaveOnActivityDestroy
    short [] myShortArray;

    @SaveOnActivityDestroy
    Short myShortObject;

    @SaveOnActivityDestroy
    Short [] myShortObjectArray;

    @SaveOnActivityDestroy
    String myString;

    @SaveOnActivityDestroy
    String [] myStringArray;

    @SaveOnActivityDestroy
    List<String> myStringList;

    @SaveOnActivityDestroy
    MySerializableBean mySerializableBean;

    @SaveOnActivityDestroy
    MySerializableBean [] mySerializableBeanArray;

    @SaveOnActivityDestroy
    MyParcelableBean myParcelableBean;

    @SaveOnActivityDestroy
    MyParcelableBean [] myParcelableBeanArray;

}
