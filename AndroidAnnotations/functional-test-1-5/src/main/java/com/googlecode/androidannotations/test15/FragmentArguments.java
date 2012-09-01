package com.googlecode.androidannotations.test15;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.Arg;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.test15.instancestate.MyGenericParcelableBean;
import com.googlecode.androidannotations.test15.instancestate.MyGenericSerializableBean;
import com.googlecode.androidannotations.test15.instancestate.MyParcelableBean;
import com.googlecode.androidannotations.test15.instancestate.MySerializableBean;

@EFragment
public class FragmentArguments extends Fragment {

	@Arg("test")
	Long nullWrappedLong = 42l;

	@Arg
	boolean myBoolean;

	@Arg
	boolean[] myBooleanArray;

	@Arg
	Boolean myBooleanObject;

	@Arg
	Boolean[] myBooleanObjectArray;

	@Arg
	byte myByte;

	@Arg
	byte[] myByteArray;

	@Arg
	Byte myByteObject;

	@Arg
	Byte[] myByteObjectArray;

	@Arg
	char myChar;

	@Arg
	char[] myCharacterArray;

	@Arg
	Character myCharacterObject;

	@Arg
	Character[] myCharacterObjectArray;

	@Arg
	CharSequence myCharSequence;

	@Arg
	double myDouble;

	@Arg
	double[] myDoubleArray;

	@Arg
	Double myDoubleObject;

	@Arg
	Double[] myDoubleObjectArray;

	@Arg
	float myFloat;

	@Arg
	float[] myFloatArray;

	@Arg
	Float myFloatObject;

	@Arg
	Float[] myFloatObjectArray;

	@Arg
	int myInt;

	@Arg
	int[] myIntegerArray;

	@Arg
	Integer myIntegerObject;

	@Arg
	Integer[] myIntegerObjectArray;

	@Arg
	ArrayList<Integer> myIntegerArrayList;

	@Arg
	long myLong;

	@Arg
	long[] myLongArray;

	@Arg
	Long myLongObject;

	@Arg
	Long[] myLongObjectArray;

	@Arg
	short myShort;

	@Arg
	short[] myShortArray;

	@Arg
	Short myShortObject;

	@Arg
	Short[] myShortObjectArray;

	@Arg
	String myString;

	@Arg
	String[] myStringArray;

	@Arg
	ArrayList<String> myStringList;

	@Arg
	MySerializableBean mySerializableBean;

	@Arg
	MySerializableBean[] mySerializableBeanArray;

	@Arg
	MyParcelableBean myParcelableBean;

	@Arg
	MyParcelableBean[] myParcelableBeanArray;

	@Arg
	Bundle myBundle;

	@Arg
	MyGenericSerializableBean<Integer> myGenericSerializableBean;

	@Arg
	MyGenericSerializableBean<Integer>[] myGenericSerializableBeanArray;

	@Arg
	MyGenericParcelableBean<String> myGenericParcelableBean;

	@Arg
	MyGenericParcelableBean<Integer>[] myGenericParcelableBeanArray;

	static {
		FragmentArguments_.create().myBundle(null).myCharSequence(null).build();
	}

}
