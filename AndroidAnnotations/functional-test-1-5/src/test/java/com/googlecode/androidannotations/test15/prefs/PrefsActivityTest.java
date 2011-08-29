/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
package com.googlecode.androidannotations.test15.prefs;

import static org.fest.assertions.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.SharedPreferences;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
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
    public void defaultValue() {
        assertThat(somePrefs.name().get()).isEqualTo("John");
    }

    @Test
    public void overridenDefaultValue() {
        assertThat(somePrefs.name().get("Smith")).isEqualTo("Smith");
    }

    @Test
    public void changesNotApplied() {
        somePrefs.edit() //
                .name() //
                .put("John");
        assertThat(sharedPref.contains("name")).isFalse();
    }
    
}
