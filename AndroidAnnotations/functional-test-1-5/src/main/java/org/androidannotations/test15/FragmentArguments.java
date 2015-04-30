/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.test15;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.test15.instancestate.MyGenericParcelableBean;
import org.androidannotations.test15.instancestate.MyGenericSerializableBean;
import org.androidannotations.test15.instancestate.MyParcelableBean;
import org.androidannotations.test15.instancestate.MySerializableBean;

import android.app.Fragment;
import android.os.Bundle;

@EFragment
public class FragmentArguments extends Fragment {

	@FragmentArg("test")
	Long nullWrappedLong = 42L;

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
	void afterInject() {

	}

	static {
		FragmentArguments_.builder().myBundle(null).myCharSequence(null).build();
	}

}
