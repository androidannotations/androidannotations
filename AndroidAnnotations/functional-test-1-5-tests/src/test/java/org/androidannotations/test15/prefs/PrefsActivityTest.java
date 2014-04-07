/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.prefs;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.SharedPreferences;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.apache.pig.impl.util.ObjectSerializer;
import org.androidannotations.test15.R;

@RunWith(AndroidAnnotationsTestRunner.class)
public class PrefsActivityTest {

	private PrefsActivity_ activity;
	private SharedPreferences sharedPref;

	private SomePrefs_ somePrefs;

	@Before
	public void setup() {
		activity = new PrefsActivity_();
		activity.onCreate(null);
		somePrefs = activity.somePrefs;
		sharedPref = somePrefs.getSharedPreferences();
	}

	@Test
	public void prefsNotNull() {
		assertThat(somePrefs).isNotNull();
	}

	@Test
	public void sharedPrefsNotNull() {
		assertThat(sharedPref).isNotNull();
	}

	@Test
	public void putString() {
		somePrefs.name().put("John");
		assertThat(sharedPref.getString("name", null)).isEqualTo("John");
	}

	@Test
	public void putInt() {
		somePrefs.age().put(42);
		assertThat(sharedPref.getInt("age", 0)).isEqualTo(42);
	}

	@Test
	public void putLong() {
		long now = System.currentTimeMillis();
		somePrefs.lastUpdated().put(now);
		assertThat(sharedPref.getLong("lastUpdated", 0)).isEqualTo(now);
	}

	@Test
	public void putStringSet() {
		Set<String> values = new TreeSet<String>();
		values.add("1");
		values.add("2");
		values.add("3");
		values.add("4");
		values.add("5");

		somePrefs.types().put(values);
		try {
			assertThat(ObjectSerializer.deserialize(sharedPref.getString("types", null))).isEqualTo(values);
		} catch (IOException e) {
			fail("IO exception while deserializing the object");
		}
	}

	@Test
	public void editLong() {
		long now = System.currentTimeMillis();

		somePrefs.edit().lastUpdated().put(now).apply();

		assertThat(sharedPref.getLong("lastUpdated", 0)).isEqualTo(now);
	}

	@Test
	public void putTwoValuesChained() {
		somePrefs.edit() //
				.name() //
				.put("John") //
				.age() //
				.put(42) //
				.apply();
		assertThat(sharedPref.getString("name", null)).isEqualTo("John");
		assertThat(sharedPref.getInt("age", 0)).isEqualTo(42);
	}

	@Test
	public void clear() {
		somePrefs.edit() //
				.name() //
				.put("John") //
				.age() //
				.put(42) //
				.apply();

		somePrefs.clear();

		assertThat(sharedPref.contains("name")).isFalse();
		assertThat(sharedPref.contains("age")).isFalse();
	}

	@Test
	public void remove() {
		somePrefs.edit() //
				.name() //
				.put("John") //
				.age() //
				.put(42) //
				.apply();

		somePrefs.name().remove();

		assertThat(sharedPref.contains("name")).isFalse();
		assertThat(sharedPref.contains("age")).isTrue();
	}

	@Test
	public void exists() {
		assertThat(somePrefs.name().exists()).isFalse();

		sharedPref.edit().putString("name", "Something").commit();

		assertThat(somePrefs.name().exists()).isTrue();
	}

	@Test
	public void getString() {
		assertThat(somePrefs.name().exists()).isFalse();

		sharedPref.edit().putString("name", "Something").commit();

		assertThat(somePrefs.name().get()).isEqualTo("Something");
	}

	@Test
	public void getLong() {
		long now = System.currentTimeMillis();

		sharedPref.edit().putLong("lastUpdated", now).commit();

		assertThat(somePrefs.lastUpdated().get()).isEqualTo(now);
	}

	@Test
	public void getStringSet() {
		Set<String> values = new TreeSet<String>();
		values.add("1");
		values.add("2");
		values.add("3");
		values.add("4");
		values.add("5");

		try {
			sharedPref.edit()
					.putString("types", ObjectSerializer.serialize((Serializable) values))
					.commit();
		} catch (IOException e) {
			fail("Error while serializing string set: " + e.toString());
		}

		Set<String> set = somePrefs.types().get();
		int i = 0;
		for(String v : set) {
			assertThat(v).isEqualTo(new Integer(++i).toString());
		}
	}

	@Test
	public void defaultValue() {
		assertThat(somePrefs.name().get()).isEqualTo("John");
	}

	@Test
	public void overridenDefaultValue() {
		assertThat(somePrefs.name().getOr("Smith")).isEqualTo("Smith");
	}

	@Test
	public void changesNotApplied() {
		somePrefs.edit() //
				.name() //
				.put("John");
		assertThat(sharedPref.contains("name")).isFalse();
	}

	@Test
	public void stringResourcePrefKey() {
		somePrefs.stringResKeyPref().put(88);
		assertThat(sharedPref.getInt(activity.getString(R.string.prefStringKey), 0)).isEqualTo(88);
	}
}
