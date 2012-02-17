package com.googlecode.androidannotations.test15.instancestate;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SaveOnActivityDestroy;
import com.googlecode.androidannotations.test15.R;

@EActivity(R.layout.main)
public class SaveInstanceStateActivity extends Activity {

	@SaveOnActivityDestroy
	Long nullWrappedLong = 42l;

	@SaveOnActivityDestroy
	boolean myBoolean;

	@SaveOnActivityDestroy
	boolean[] myBooleanArray;

	@SaveOnActivityDestroy
	Boolean myBooleanObject;

	@SaveOnActivityDestroy
	Boolean[] myBooleanObjectArray;

	@SaveOnActivityDestroy
	byte myByte;

	@SaveOnActivityDestroy
	byte[] myByteArray;

	@SaveOnActivityDestroy
	Byte myByteObject;

	@SaveOnActivityDestroy
	Byte[] myByteObjectArray;

	@SaveOnActivityDestroy
	char myChar;

	@SaveOnActivityDestroy
	char[] myCharacterArray;

	@SaveOnActivityDestroy
	Character myCharacterObject;

	@SaveOnActivityDestroy
	Character[] myCharacterObjectArray;

	@SaveOnActivityDestroy
	CharSequence myCharSequence;

	@SaveOnActivityDestroy
	double myDouble;

	@SaveOnActivityDestroy
	double[] myDoubleArray;

	@SaveOnActivityDestroy
	Double myDoubleObject;

	@SaveOnActivityDestroy
	Double[] myDoubleObjectArray;

	@SaveOnActivityDestroy
	float myFloat;

	@SaveOnActivityDestroy
	float[] myFloatArray;

	@SaveOnActivityDestroy
	Float myFloatObject;

	@SaveOnActivityDestroy
	Float[] myFloatObjectArray;

	@SaveOnActivityDestroy
	int myInt;

	@SaveOnActivityDestroy
	int[] myIntegerArray;

	@SaveOnActivityDestroy
	Integer myIntegerObject;

	@SaveOnActivityDestroy
	Integer[] myIntegerObjectArray;

	@SaveOnActivityDestroy
	ArrayList<Integer> myIntegerArrayList;

	@SaveOnActivityDestroy
	long myLong;

	@SaveOnActivityDestroy
	long[] myLongArray;

	@SaveOnActivityDestroy
	Long myLongObject;

	@SaveOnActivityDestroy
	Long[] myLongObjectArray;

	@SaveOnActivityDestroy
	short myShort;

	@SaveOnActivityDestroy
	short[] myShortArray;

	@SaveOnActivityDestroy
	Short myShortObject;

	@SaveOnActivityDestroy
	Short[] myShortObjectArray;

	@SaveOnActivityDestroy
	String myString;

	@SaveOnActivityDestroy
	String[] myStringArray;

	@SaveOnActivityDestroy
	ArrayList<String> myStringList;

	@SaveOnActivityDestroy
	MySerializableBean mySerializableBean;

	@SaveOnActivityDestroy
	MySerializableBean[] mySerializableBeanArray;

	@SaveOnActivityDestroy
	MyParcelableBean myParcelableBean;

	@SaveOnActivityDestroy
	MyParcelableBean[] myParcelableBeanArray;

	@SaveOnActivityDestroy
	Bundle myBundle;

	/*
	 * This should be solved before we merge this feature. We should also create
	 * the associated test.
	 */
	// @SaveOnActivityDestroy
	// MyGenericSerializableBeasn<MySerializableBean> myGenericSerializableBean;

}
