package com.googlecode.androidannotations.test15;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.test15.instancestate.MyGenericParcelableBean;
import com.googlecode.androidannotations.test15.instancestate.MyGenericSerializableBean;
import com.googlecode.androidannotations.test15.instancestate.MyParcelableBean;
import com.googlecode.androidannotations.test15.instancestate.MySerializableBean;

@EFragment
public class FragmentArguments extends Fragment {

	@FragmentArg("test")
	Long nullWrappedLong = 42l;

	@FragmentArg
	boolean myBoolean;

	@FragmentArg
	boolean[] myBooleanArray;

	@FragmentArg
	Boolean myBooleanObject;

	@FragmentArg
	Boolean[] myBooleanObjectArray;

	@FragmentArg
	byte myByte;

	@FragmentArg
	byte[] myByteArray;

	@FragmentArg
	Byte myByteObject;

	@FragmentArg
	Byte[] myByteObjectArray;

	@FragmentArg
	char myChar;

	@FragmentArg
	char[] myCharacterArray;

	@FragmentArg
	Character myCharacterObject;

	@FragmentArg
	Character[] myCharacterObjectArray;

	@FragmentArg
	CharSequence myCharSequence;

	@FragmentArg
	double myDouble;

	@FragmentArg
	double[] myDoubleArray;

	@FragmentArg
	Double myDoubleObject;

	@FragmentArg
	Double[] myDoubleObjectArray;

	@FragmentArg
	float myFloat;

	@FragmentArg
	float[] myFloatArray;

	@FragmentArg
	Float myFloatObject;

	@FragmentArg
	Float[] myFloatObjectArray;

	@FragmentArg
	int myInt;

	@FragmentArg
	int[] myIntegerArray;

	@FragmentArg
	Integer myIntegerObject;

	@FragmentArg
	Integer[] myIntegerObjectArray;

	@FragmentArg
	ArrayList<Integer> myIntegerArrayList;

	@FragmentArg
	long myLong;

	@FragmentArg
	long[] myLongArray;

	@FragmentArg
	Long myLongObject;

	@FragmentArg
	Long[] myLongObjectArray;

	@FragmentArg
	short myShort;

	@FragmentArg
	short[] myShortArray;

	@FragmentArg
	Short myShortObject;

	@FragmentArg
	Short[] myShortObjectArray;

	@FragmentArg
	String myString;

	@FragmentArg
	String[] myStringArray;

	@FragmentArg
	ArrayList<String> myStringList;

	@FragmentArg
	MySerializableBean mySerializableBean;

	@FragmentArg
	MySerializableBean[] mySerializableBeanArray;

	@FragmentArg
	MyParcelableBean myParcelableBean;

	@FragmentArg
	MyParcelableBean[] myParcelableBeanArray;

	@FragmentArg
	Bundle myBundle;

	@FragmentArg
	MyGenericSerializableBean<Integer> myGenericSerializableBean;

	@FragmentArg
	MyGenericSerializableBean<Integer>[] myGenericSerializableBeanArray;

	@FragmentArg
	MyGenericParcelableBean<String> myGenericParcelableBean;

	@FragmentArg
	MyGenericParcelableBean<Integer>[] myGenericParcelableBeanArray;
	
	@AfterInject
	void afterInject(){
		
	}

	static {
		FragmentArguments_.builder().myBundle(null).myCharSequence(null).build();
	}

}
