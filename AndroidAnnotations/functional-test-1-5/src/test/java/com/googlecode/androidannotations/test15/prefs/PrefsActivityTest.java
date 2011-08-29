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
