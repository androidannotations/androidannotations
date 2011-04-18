package com.googlecode.androidannotations.test15;

import static com.googlecode.androidannotations.test15.MyAssertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class EmptyActivityWithoutLayoutTest {

    @Test
    public void shouldHaveNoLayoutAfterCreate() {
    	EmptyActivityWithoutLayout_ activity = new EmptyActivityWithoutLayout_();
    	
    	assertThat(activity.findViewById(R.id.helloTextView)).isNull();
    	
    	activity.onCreate(null);
    	
    	assertThat(activity.findViewById(R.id.helloTextView)).isNull();
    }
    
    
}
