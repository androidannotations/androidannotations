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
package org.androidannotations.test15.instancestate;

import java.util.ArrayList;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.test15.R;

import android.app.Activity;
import android.os.Bundle;

@EActivity(R.layout.main)
public class SaveInstanceStateActivity extends Activity {

	@InstanceState
	Long nullWrappedLong = 42L;

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

	@InstanceState
	MyGenericSerializableBean<Integer> myGenericSerializableBean;

	@InstanceState
	MyGenericSerializableBean<Integer>[] myGenericSerializableBeanArray;

	@InstanceState
	MyGenericParcelableBean<String> myGenericParcelableBean;

	@InstanceState
	MyGenericParcelableBean<Integer>[] myGenericParcelableBeanArray;

	@InstanceState
	ArrayList<MyParcelableBean> myParcelableBeanArrayList;

	@InstanceState
	ArrayList<MyGenericParcelableBean<Integer>> myGenericParcelableBeanArrayList;

	@InstanceState
	ArrayList<MySerializableBean> mySerializableBeanArrayList;

	@InstanceState
	Bundle bundle;

}
