/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
