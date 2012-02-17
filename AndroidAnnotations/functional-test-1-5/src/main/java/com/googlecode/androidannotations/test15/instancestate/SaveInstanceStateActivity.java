package com.googlecode.androidannotations.test15.instancestate;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.InstanceState;
import com.googlecode.androidannotations.test15.R;

@EActivity(R.layout.main)
public class SaveInstanceStateActivity extends Activity {

	@InstanceState
	Long nullWrappedLong = 42l;

	@InstanceState
	boolean myBoolean;

	@InstanceState
	boolean[] myBooleanArray;

	@InstanceState
	Boolean myBooleanObject;

	@InstanceState
	Boolean[] myBooleanObjectArray;

	@InstanceState
	byte myByte;

	@InstanceState
	byte[] myByteArray;

	@InstanceState
	Byte myByteObject;

	@InstanceState
	Byte[] myByteObjectArray;

	@InstanceState
	char myChar;

	@InstanceState
	char[] myCharacterArray;

	@InstanceState
	Character myCharacterObject;

	@InstanceState
	Character[] myCharacterObjectArray;

	@InstanceState
	CharSequence myCharSequence;

	@InstanceState
	double myDouble;

	@InstanceState
	double[] myDoubleArray;

	@InstanceState
	Double myDoubleObject;

	@InstanceState
	Double[] myDoubleObjectArray;

	@InstanceState
	float myFloat;

	@InstanceState
	float[] myFloatArray;

	@InstanceState
	Float myFloatObject;

	@InstanceState
	Float[] myFloatObjectArray;

	@InstanceState
	int myInt;

	@InstanceState
	int[] myIntegerArray;

	@InstanceState
	Integer myIntegerObject;

	@InstanceState
	Integer[] myIntegerObjectArray;

	@InstanceState
	ArrayList<Integer> myIntegerArrayList;

	@InstanceState
	long myLong;

	@InstanceState
	long[] myLongArray;

	@InstanceState
	Long myLongObject;

	@InstanceState
	Long[] myLongObjectArray;

	@InstanceState
	short myShort;

	@InstanceState
	short[] myShortArray;

	@InstanceState
	Short myShortObject;

	@InstanceState
	Short[] myShortObjectArray;

	@InstanceState
	String myString;

	@InstanceState
	String[] myStringArray;

	@InstanceState
	ArrayList<String> myStringList;

	@InstanceState
	MySerializableBean mySerializableBean;

	@InstanceState
	MySerializableBean[] mySerializableBeanArray;

	@InstanceState
	MyParcelableBean myParcelableBean;

	@InstanceState
	MyParcelableBean[] myParcelableBeanArray;

	@InstanceState
	Bundle myBundle;

	/*
	 * This should be solved before we merge this feature. We should also create
	 * the associated test.
	 */
	// @InstanceState
	// MyGenericSerializableBeasn<MySerializableBean> myGenericSerializableBean;

}
