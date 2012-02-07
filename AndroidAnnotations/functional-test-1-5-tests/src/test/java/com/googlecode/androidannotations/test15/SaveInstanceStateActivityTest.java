/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.Bundle;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.CustomShadowBundle;

@RunWith(AndroidAnnotationsTestRunner.class)
public class SaveInstanceStateActivityTest {

	private boolean					myBoolean				= true;
	private boolean[]				myBooleanArray			= new boolean[] { true, false, true };
	private Boolean					myBooleanObject			= Boolean.TRUE;
	private Boolean[]				myBooleanObjectArray	= new Boolean[] { true, false, true };
	private byte					myByte					= 8;
	private byte[]					myByteArray				= new byte[] { 3, 8, 6 };
	private Byte					myByteObject			= Byte.MAX_VALUE;
	private Byte[]					myByteObjectArray		= new Byte[] { Byte.MAX_VALUE, Byte.MIN_VALUE, 9 };
	private char					myChar					= 'c';
	private char[]					myCharacterArray		= new char[] { 'a', 'b', 'c' };
	private Character				myCharacterObject		= Character.MIN_SURROGATE;
	private Character[]				myCharacterObjectArray	= new Character[] { 'a', 'c', 'b' };
	private CharSequence			myCharSequence			= "S5";
	// Only available since API level 8
	// CharSequence [] myCharSequenceArray;
	// ArrayList<CharSequence> myCharSequenceArray;
	private double					myDouble				= 1.05d;
	private double[]				myDoubleArray			= new double[] { 1.05d, 2.03d };
	private Double					myDoubleObject			= 1.08d;																				;
	private Double[]				myDoubleObjectArray		= new Double[] { 1.05d, 2.03d };
	private float					myFloat					= 3.7f;
	private float[]					myFloatArray			= new float[] { 3.7f, 3.8f };
	private Float					myFloatObject			= 3.4f;
	private Float[]					myFloatObjectArray		= new Float[] { 3.6f, 4.6f };
	private int						myInt					= 12;
	private int[]					myIntegerArray			= new int[] { 3, 5, 9 };
	private Integer					myIntegerObject			= 64;
	private Integer[]				myIntegerObjectArray	= new Integer[] { 7, 45, 14 };
	private ArrayList<Integer>		myIntegerArrayList		= new ArrayList<Integer>();
	private long					myLong					= 5;
	private long[]					myLongArray				= new long[] { 3, 6, 9 };
	private Long					myLongObject			= 8l;
	private Long[]					myLongObjectArray		= new Long[] { 3l, 6l, 9l };
	private short					myShort					= 124;
	private short[]					myShortArray			= new short[] { 3, 6, 18 };
	private Short					myShortObject			= 9;
	private Short[]					myShortObjectArray		= new Short[] { 3, 6, 18 };
	private String					myString				= "S4";
	private String[]				myStringArray			= new String[] { "S1", "S3" };
	private ArrayList<String>		myStringList			= new ArrayList<String>();
	private MySerializableBean		mySerializableBean		= new MySerializableBean(4);
	private MySerializableBean[]	mySerializableBeanArray	= new MySerializableBean[] { new MySerializableBean(5), new MySerializableBean(6) };
	private MyParcelableBean		myParcelableBean		= new MyParcelableBean(9);
	private MyParcelableBean[]		myParcelableBeanArray	= new MyParcelableBean[] { new MyParcelableBean(3), new MyParcelableBean(9) };
	private Bundle					myBundle				= new Bundle();

	{
		myIntegerArrayList.add(5);
		myIntegerArrayList.add(8);

		myStringList.add("S1");
		myStringList.add("S2");

		myBundle.putIntArray("myIntegerArray", myIntegerArray);
	}

	@Before
	public void setup() {
		Robolectric.bindShadowClass(CustomShadowBundle.class);
	}

	@Test
	public void testRestoreDataOnCreateActivity() {
		SaveInstanceStateActivity_ activity = new SaveInstanceStateActivity_();

		Bundle savedInstanceState = createBundle();
		assertThat(activity.myBoolean).isNotEqualTo(myBoolean);
		assertThat(activity.myBooleanArray).isNotEqualTo(myBooleanArray);
		assertThat(activity.myBooleanObject).isNotEqualTo(myBooleanObject);
		assertThat(activity.myBooleanObjectArray).isNotEqualTo(myBooleanObjectArray);
		assertThat(activity.myByte).isNotEqualTo(myByte);
		assertThat(activity.myByteArray).isNotEqualTo(myByteArray);
		assertThat(activity.myByteObject).isNotEqualTo(myByteObject);
		assertThat(activity.myByteObjectArray).isNotEqualTo(myByteObjectArray);
		assertThat(activity.myChar).isNotEqualTo(myChar);
		assertThat(activity.myCharacterArray).isNotEqualTo(myCharacterArray);
		assertThat(activity.myCharacterObject).isNotEqualTo(myCharacterObject);
		assertThat(activity.myCharacterObjectArray).isNotEqualTo(myCharacterObjectArray);
		assertThat(activity.myCharSequence).isNotEqualTo(myCharSequence);
		// Only available since API level 8
		// assertThat(activity.myCharSequenceArray).isNotEqualTo(myCharSequenceArray);
		// assertThat(activity.myCharSequenceArray).isNotEqualTo(myCharSequenceArray);
		assertThat(activity.myDouble).isNotEqualTo(myDouble);
		assertThat(activity.myDoubleArray).isNotEqualTo(myDoubleArray);
		assertThat(activity.myDoubleObject).isNotEqualTo(myDoubleObject);
		assertThat(activity.myDoubleObjectArray).isNotEqualTo(myDoubleObjectArray);
		assertThat(activity.myFloat).isNotEqualTo(myFloat);
		assertThat(activity.myFloatArray).isNotEqualTo(myFloatArray);
		assertThat(activity.myFloatObject).isNotEqualTo(myFloatObject);
		assertThat(activity.myFloatObjectArray).isNotEqualTo(myFloatObjectArray);
		assertThat(activity.myInt).isNotEqualTo(myInt);
		assertThat(activity.myIntegerArray).isNotEqualTo(myIntegerArray);
		assertThat(activity.myIntegerObject).isNotEqualTo(myIntegerObject);
		assertThat(activity.myIntegerObjectArray).isNotEqualTo(myIntegerObjectArray);
		assertThat(activity.myIntegerArrayList).isNotEqualTo(myIntegerArrayList);
		assertThat(activity.myLong).isNotEqualTo(myLong);
		assertThat(activity.myLongArray).isNotEqualTo(myLongArray);
		assertThat(activity.myLongObject).isNotEqualTo(myLongObject);
		assertThat(activity.myLongObjectArray).isNotEqualTo(myLongObjectArray);
		assertThat(activity.myShort).isNotEqualTo(myShort);
		assertThat(activity.myShortArray).isNotEqualTo(myShortArray);
		assertThat(activity.myShortObject).isNotEqualTo(myShortObject);
		assertThat(activity.myShortObjectArray).isNotEqualTo(myShortObjectArray);
		assertThat(activity.myString).isNotEqualTo(myString);
		assertThat(activity.myStringArray).isNotEqualTo(myStringArray);
		assertThat(activity.myStringList).isNotEqualTo(myStringList);
		assertThat(activity.mySerializableBean).isNotEqualTo(mySerializableBean);
		assertThat(activity.mySerializableBeanArray).isNotEqualTo(mySerializableBeanArray);
		assertThat(activity.myParcelableBean).isNotEqualTo(myParcelableBean);
		assertThat(activity.myParcelableBeanArray).isNotEqualTo(myParcelableBeanArray);
		assertThat(activity.myBundle).isNotEqualTo(myBundle);

		activity.onCreate(savedInstanceState);

		assertThat(activity.myBoolean).isEqualTo(myBoolean);
		assertThat(activity.myBooleanArray).isEqualTo(myBooleanArray);
		assertThat(activity.myBooleanObject).isEqualTo(myBooleanObject);
		assertThat(activity.myBooleanObjectArray).isEqualTo(myBooleanObjectArray);
		assertThat(activity.myByte).isEqualTo(myByte);
		assertThat(activity.myByteArray).isEqualTo(myByteArray);
		assertThat(activity.myByteObject).isEqualTo(myByteObject);
		assertThat(activity.myByteObjectArray).isEqualTo(myByteObjectArray);
		assertThat(activity.myChar).isEqualTo(myChar);
		assertThat(activity.myCharacterArray).isEqualTo(myCharacterArray);
		assertThat(activity.myCharacterObject).isEqualTo(myCharacterObject);
		assertThat(activity.myCharacterObjectArray).isEqualTo(myCharacterObjectArray);
		assertThat(activity.myCharSequence).isEqualTo(myCharSequence);
		// Only available since API level 8
		// assertThat(activity.myCharSequenceArray).isEqualTo(myCharSequenceArray);
		// assertThat(activity.myCharSequenceArray).isEqualTo(myCharSequenceArray);
		assertThat(activity.myDouble).isEqualTo(myDouble);
		assertThat(activity.myDoubleArray).isEqualTo(myDoubleArray);
		assertThat(activity.myDoubleObject).isEqualTo(myDoubleObject);
		assertThat(activity.myDoubleObjectArray).isEqualTo(myDoubleObjectArray);
		assertThat(activity.myFloat).isEqualTo(myFloat);
		assertThat(activity.myFloatArray).isEqualTo(myFloatArray);
		assertThat(activity.myFloatObject).isEqualTo(myFloatObject);
		assertThat(activity.myFloatObjectArray).isEqualTo(myFloatObjectArray);
		assertThat(activity.myInt).isEqualTo(myInt);
		assertThat(activity.myIntegerArray).isEqualTo(myIntegerArray);
		assertThat(activity.myIntegerObject).isEqualTo(myIntegerObject);
		assertThat(activity.myIntegerObjectArray).isEqualTo(myIntegerObjectArray);
		assertThat(activity.myIntegerArrayList).isEqualTo(myIntegerArrayList);
		assertThat(activity.myLong).isEqualTo(myLong);
		assertThat(activity.myLongArray).isEqualTo(myLongArray);
		assertThat(activity.myLongObject).isEqualTo(myLongObject);
		assertThat(activity.myLongObjectArray).isEqualTo(myLongObjectArray);
		assertThat(activity.myShort).isEqualTo(myShort);
		assertThat(activity.myShortArray).isEqualTo(myShortArray);
		assertThat(activity.myShortObject).isEqualTo(myShortObject);
		assertThat(activity.myShortObjectArray).isEqualTo(myShortObjectArray);
		assertThat(activity.myString).isEqualTo(myString);
		assertThat(activity.myStringArray).isEqualTo(myStringArray);
		assertThat(activity.myStringList).isEqualTo(myStringList);
		assertThat(activity.mySerializableBean).isEqualTo(mySerializableBean);
		assertThat(activity.mySerializableBeanArray).isEqualTo(mySerializableBeanArray);
		assertThat(activity.myParcelableBean).isEqualTo(myParcelableBean);
		assertThat(activity.myParcelableBeanArray).isEqualTo(myParcelableBeanArray);
		assertThat(activity.myBundle).isEqualTo(myBundle);

		Bundle bundle = new Bundle();
		activity.onSaveInstanceState(bundle);

		assertThat(bundle.getBoolean("myBoolean")).isEqualTo(myBoolean);
		assertThat(bundle.getBooleanArray("myBooleanArray")).isEqualTo(myBooleanArray);
		assertThat(bundle.getBoolean("myBooleanObject")).isEqualTo(myBooleanObject);
		assertThat(bundle.getSerializable("myBooleanObjectArray")).isEqualTo(myBooleanObjectArray);
		assertThat(bundle.getByte("myByte")).isEqualTo(myByte);
		assertThat(bundle.getByteArray("myByteArray")).isEqualTo(myByteArray);
		assertThat(bundle.getByte("myByteObject")).isEqualTo(myByteObject);
		assertThat(bundle.getSerializable("myByteObjectArray")).isEqualTo(myByteObjectArray);
		assertThat(bundle.getChar("myChar")).isEqualTo(myChar);
		assertThat(bundle.getCharArray("myCharacterArray")).isEqualTo(myCharacterArray);
		assertThat(bundle.getChar("myCharacterObject")).isEqualTo(myCharacterObject);
		assertThat(bundle.getSerializable("myCharacterObjectArray")).isEqualTo(myCharacterObjectArray);
		assertThat(bundle.getCharSequence("myCharSequence")).isEqualTo(myCharSequence);
		// Only available since API level 8
		// assertThat(bundle.getCharSequenceArray("myCharSequenceArray")).isEqualTo(myCharSequenceArray);
		// assertThat(bundle.getCharSequenceArray("myCharSequenceArray")).isEqualTo(myCharSequenceArray);
		assertThat(bundle.getDouble("myDouble")).isEqualTo(myDouble);
		assertThat(bundle.getDoubleArray("myDoubleArray")).isEqualTo(myDoubleArray);
		assertThat(bundle.getDouble("myDoubleObject")).isEqualTo(myDoubleObject);
		assertThat(bundle.getSerializable("myDoubleObjectArray")).isEqualTo(myDoubleObjectArray);
		assertThat(bundle.getFloat("myFloat")).isEqualTo(myFloat);
		assertThat(bundle.getFloatArray("myFloatArray")).isEqualTo(myFloatArray);
		assertThat(bundle.getFloat("myFloatObject")).isEqualTo(myFloatObject);
		assertThat(bundle.getSerializable("myFloatObjectArray")).isEqualTo(myFloatObjectArray);
		assertThat(bundle.getInt("myInt")).isEqualTo(myInt);
		assertThat(bundle.getIntArray("myIntegerArray")).isEqualTo(myIntegerArray);
		assertThat(bundle.getInt("myIntegerObject")).isEqualTo(myIntegerObject);
		assertThat(bundle.getSerializable("myIntegerObjectArray")).isEqualTo(myIntegerObjectArray);
		assertThat(bundle.getIntegerArrayList("myIntegerArrayList")).isEqualTo(myIntegerArrayList);
		assertThat(bundle.getLong("myLong")).isEqualTo(myLong);
		assertThat(bundle.getLongArray("myLongArray")).isEqualTo(myLongArray);
		assertThat(bundle.getLong("myLongObject")).isEqualTo(myLongObject);
		assertThat(bundle.getSerializable("myLongObjectArray")).isEqualTo(myLongObjectArray);
		assertThat(bundle.getShort("myShort")).isEqualTo(myShort);
		assertThat(bundle.getShortArray("myShortArray")).isEqualTo(myShortArray);
		assertThat(bundle.getShort("myShortObject")).isEqualTo(myShortObject);
		assertThat(bundle.getSerializable("myShortObjectArray")).isEqualTo(myShortObjectArray);
		assertThat(bundle.getString("myString")).isEqualTo(myString);
		assertThat(bundle.getStringArray("myStringArray")).isEqualTo(myStringArray);
		assertThat(bundle.getStringArrayList("myStringList")).isEqualTo(myStringList);
		assertThat(bundle.getSerializable("mySerializableBean")).isEqualTo(mySerializableBean);
		assertThat(bundle.getSerializable("mySerializableBeanArray")).isEqualTo(mySerializableBeanArray);
		assertThat(bundle.getParcelable("myParcelableBean")).isEqualTo(myParcelableBean);
		assertThat(bundle.getParcelableArray("myParcelableBeanArray")).isEqualTo(myParcelableBeanArray);
		assertThat(bundle.getBundle("myBundle")).isEqualTo(myBundle);

		Bundle emptyBundle = new Bundle();
		assertThat(emptyBundle).isNotEqualTo(savedInstanceState);
		assertThat(emptyBundle).isNotEqualTo(bundle);

		assertThat(bundle).isEqualTo(savedInstanceState);
	}
	
	@Test
	public void testWithNullValues() {
		SaveInstanceStateActivity_ activity = new SaveInstanceStateActivity_();
		
		Bundle bundle = new Bundle();
		activity.onSaveInstanceState(bundle);
		activity.onCreate(bundle);
	}

	@Test
	public void testWithoutSavedState() {
		SaveInstanceStateActivity_ activity = new SaveInstanceStateActivity_();
		activity.onCreate(null);
	}

	private Bundle createBundle() {
		Bundle b = new Bundle();
		b.putBoolean("myBoolean", myBoolean);
		b.putBoolean("myBooleanObject", myBooleanObject);
		b.putBooleanArray("myBooleanArray", myBooleanArray);
		b.putBundle("myBundle", myBundle);
		b.putByte("myByte", myByte);
		b.putByte("myByteObject", myByteObject);
		b.putByteArray("myByteArray", myByteArray);
		b.putChar("myChar", myChar);
		b.putChar("myCharacterObject", myCharacterObject);
		b.putCharArray("myCharacterArray", myCharacterArray);
		b.putCharSequence("myCharSequence", myCharSequence);
		b.putDouble("myDouble", myDouble);
		b.putDoubleArray("myDoubleArray", myDoubleArray);
		b.putDouble("myDoubleObject", myDoubleObject);
		b.putFloat("myFloat", myFloat);
		b.putFloat("myFloatObject", myFloatObject);
		b.putFloatArray("myFloatArray", myFloatArray);
		b.putInt("myIntegerObject", myIntegerObject);
		b.putInt("myInt", myInt);
		b.putIntArray("myIntegerArray", myIntegerArray);
		b.putIntegerArrayList("myIntegerArrayList", myIntegerArrayList);
		b.putLong("myLong", myLong);
		b.putLong("myLongObject", myLongObject);
		b.putLongArray("myLongArray", myLongArray);
		b.putParcelable("myParcelableBean", myParcelableBean);
		b.putParcelableArray("myParcelableBeanArray", myParcelableBeanArray);
		b.putSerializable("myBooleanObjectArray", myBooleanObjectArray);
		b.putSerializable("myByteObjectArray", myByteObjectArray);
		b.putSerializable("myCharacterObjectArray", myCharacterObjectArray);
		b.putSerializable("myDoubleObjectArray", myDoubleObjectArray);
		b.putSerializable("myFloatObjectArray", myFloatObjectArray);
		b.putSerializable("myIntegerObjectArray", myIntegerObjectArray);
		b.putSerializable("myLongObjectArray", myLongObjectArray);
		b.putSerializable("mySerializableBeanArray", mySerializableBeanArray);
		b.putSerializable("mySerializableBean", mySerializableBean);
		b.putShort("myShort", myShort);
		b.putShort("myShortObject", myShortObject);
		b.putShortArray("myShortArray", myShortArray);
		b.putString("myString", myString);
		b.putStringArrayList("myStringList", myStringList);
		b.putStringArray("myStringArray", myStringArray);
		b.putSerializable("myShortObjectArray", myShortObjectArray);
		return b;
	}

}
