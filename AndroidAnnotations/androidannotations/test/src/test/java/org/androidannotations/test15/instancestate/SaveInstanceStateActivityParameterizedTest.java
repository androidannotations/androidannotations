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

import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.androidannotations.test15.CustomShadowBundle;
import org.fest.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters;
import org.robolectric.ParameterizedRobolectricTestRunnerWorkaround;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import android.os.Bundle;

@Config(shadows = CustomShadowBundle.class)
@RunWith(ParameterizedRobolectricTestRunnerWorkaround.class)
public class SaveInstanceStateActivityParameterizedTest {

	@Parameters(name = "{0}")
	public static Collection<Object[]> generateTestCases() throws Exception {
		ArrayList<MyGenericParcelableBean<Integer>> myGenericParcelableBeanArrayList = new ArrayList<MyGenericParcelableBean<Integer>>();
		myGenericParcelableBeanArrayList.add(new MyGenericParcelableBean<Integer>(1));
		myGenericParcelableBeanArrayList.add(new MyGenericParcelableBean<Integer>(2));
		myGenericParcelableBeanArrayList.add(new MyGenericParcelableBean<Integer>(3));

		// CHECKSTYLE:OFF
		Object[][] testCases = { //
				//
				{ "myBoolean", true }, //
				{ "myBooleanArray", new boolean[] { true, false, true } }, //
				{ "myBooleanObject", Boolean.TRUE }, //
				{ "myByte", (byte) 8 }, //
				{ "myByteArray", new byte[] { 3, 8, 6 } }, //
				{ "myByteObject", Byte.MAX_VALUE }, //
				{ "myByteObjectArray", new Byte[] { Byte.MAX_VALUE, Byte.MIN_VALUE, 9 } }, //
				{ "myChar", 'c' }, //
				{ "myCharacterArray", new char[] { 'a', 'b', 'c' } }, //
				{ "myCharacterObject", Character.MIN_SURROGATE }, //
				{ "myCharacterObjectArray", new Character[] { 'a', 'c', 'b' } }, //
				{ "myCharSequence", "S5" }, //
				{ "myDouble", 1.05d }, //
				{ "myDoubleArray", new double[] { 1.05d, 2.03d } }, //
				{ "myDoubleObject", 1.08d }, //
				{ "myDoubleObjectArray", new Double[] { 1.05d, 2.03d } }, //
				{ "myFloat", 3.7f }, //
				{ "myFloatArray", new float[] { 3.7f, 3.8f } }, //
				{ "myFloatObject", 3.4f }, //
				{ "myFloatObjectArray", new Float[] { 3.6f, 4.6f } }, //
				{ "myInt", 12 }, //
				{ "myIntegerArray", new int[] { 3, 5, 9 } }, //
				{ "myIntegerObject", 64 }, //
				{ "myIntegerObjectArray", new Integer[] { 7, 45, 14 } }, //
				{ "myIntegerArrayList", Lists.newArrayList(5, 8) }, //
				{ "myLong", 5l }, //
				{ "myLongArray", new long[] { 3, 6, 9 } }, //
				{ "myLongObject", 8l }, //
				{ "myLongObjectArray", new Long[] { 3l, 6l, 9l } }, //
				{ "myShort", (short) 124 }, //
				{ "myShortArray", new short[] { 3, 6, 18 } }, //
				{ "myShortObject", (short) 9 }, //
				{ "myShortObjectArray", new Short[] { 3, 6, 18 } }, //
				{ "myString", "S4" }, //
				{ "myStringArray", new String[] { "S1", "S3" } }, //
				{ "myStringList", Lists.newArrayList("S1", "S2") }, //
				{ "mySerializableBean", new MySerializableBean(4) }, //
				{ "mySerializableBeanArray", new MySerializableBean[] { new MySerializableBean(5), new MySerializableBean(6) } }, //
				{ "myParcelableBean", new MyParcelableBean(9) }, //
				{ "myParcelableBeanArray", new MyParcelableBean[] { new MyParcelableBean(3), new MyParcelableBean(9) } }, //
				{ "myGenericSerializableBean", new MyGenericSerializableBean<Integer>(3) }, //
				{ "myGenericSerializableBeanArray", new MyGenericSerializableBean[] { new MyGenericSerializableBean<Integer>(3), new MyGenericSerializableBean<Integer>(5) } }, //
				{ "myGenericParcelableBean", new MyGenericParcelableBean<String>("Plop !") }, //
				{ "myGenericParcelableBeanArray", new MyGenericParcelableBean[] { new MyGenericParcelableBean<Integer>(3), new MyGenericParcelableBean<Integer>(5) } }, //
				{ "myParcelableBeanArrayList", Lists.newArrayList(new MyParcelableBean(1), new MyParcelableBean(2), new MyParcelableBean(3)) }, //
				{ "myGenericParcelableBeanArrayList", myGenericParcelableBeanArrayList }, //
				{ "mySerializableBeanArrayList", Lists.newArrayList(new MySerializableBean(1), new MySerializableBean(2), new MySerializableBean(3)) }, //
				{ "nullWrappedLong", null }, //
		};
		// CHECKSTYLE:ON

		return Arrays.asList(testCases);
	}

	private Object value;
	private String fieldName;
	private Field field;

	/**
	 * @see RobolectricParameterized
	 */
	public SaveInstanceStateActivityParameterizedTest(String fieldName, Object value) throws Exception {
		this.fieldName = fieldName;
		this.value = value;
		field = SaveInstanceStateActivity.class.getDeclaredField(fieldName);
		field.setAccessible(true);
	}

	@Test
	public void canSaveField() throws Exception {
		SaveInstanceStateActivity_ savedActivity = Robolectric.buildActivity(SaveInstanceStateActivity_.class).create().get();

		Bundle bundle = saveField(savedActivity);

		assertThat(bundle.get(fieldName)).isEqualTo(value);
	}

	@Test
	public void canLoadField() throws Exception {
		SaveInstanceStateActivity_ savedActivity = Robolectric.buildActivity(SaveInstanceStateActivity_.class).create().get();

		Bundle bundle = saveField(savedActivity);

		SaveInstanceStateActivity_ recreatedActivity = Robolectric.buildActivity(SaveInstanceStateActivity_.class).create().get();

		Object initialFieldValue = field.get(recreatedActivity);

		assertThat(initialFieldValue).isNotEqualTo(value);

		recreatedActivity.onCreate(bundle);

		Object loadedFieldValue = field.get(recreatedActivity);

		assertThat(loadedFieldValue).isEqualTo(value);
	}

	private Bundle saveField(SaveInstanceStateActivity_ savedActivity) throws NoSuchFieldException, IllegalAccessException {
		field.set(savedActivity, value);
		Bundle bundle = new Bundle();
		savedActivity.onSaveInstanceState(bundle);
		return bundle;
	}

}
