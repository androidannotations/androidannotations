package com.googlecode.androidannotations.test15;


import static org.fest.assertions.Assertions.*;

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
    	assertThat(activity.stringExtra).isEqualTo("Hello !");
    }
    
    @Test
    public void array_extra_injected() {
        CustomData[] customData = {new CustomData("42")};
        intent.putExtra("arrayExtra", customData);
        activity.onCreate(null);
        assertThat(activity.arrayExtra).isEqualTo(customData);
    }
    
    
}
