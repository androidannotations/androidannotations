package com.googlecode.androidannotations.test15;

import static org.fest.assertions.Assertions.assertThat;
import static com.googlecode.androidannotations.test15.MyAssertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class EmptyActivityWithLayoutTest {

    @Test
    public void shouldHaveLayoutAfterCreate() {
    	EmptyActivityWithLayout_ activity = new EmptyActivityWithLayout_();
    	
    	assertThat(activity.findViewById(R.id.helloTextView)).isNull();
    	
    	activity.onCreate(null);
    	
    	assertThat(activity.findViewById(R.id.helloTextView)).hasId(R.id.helloTextView);
    }
    
    
}
