package com.googlecode.androidannotations.test15;


import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class InjectExtraTest {
    
    private Intent intent;
    private ExtraInjectedActivity_ activity;

    @Before
    public void setup() {
        activity = new ExtraInjectedActivity_();
        intent = new Intent();
        activity.setIntent(intent);
    }

    @Test
    public void simple_string_extra_injected() {
        intent.putExtra("stringExtra", "Hello !");
    	activity.onCreate(null);
    	Assertions.assertThat(activity.simpleExtra).isEqualTo("Hello !");
    }
    
    
}
