package com.googlecode.androidannotations.test15;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ApplicationInjectedActivityTest {

    @Test
    public void shouldHaveLayoutAfterCreate() {
        ApplicationInjectedActivity_ activity = new ApplicationInjectedActivity_();
    	
    	activity.onCreate(null);
    	
    	assertThat(activity.customApplication).isNotNull();
    }
    
    
}
