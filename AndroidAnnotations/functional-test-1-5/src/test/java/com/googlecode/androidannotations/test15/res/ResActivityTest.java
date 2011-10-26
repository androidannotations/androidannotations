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
package com.googlecode.androidannotations.test15.res;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.res.Resources;
import android.view.animation.AnimationUtils;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ResActivityTest {

    private ResActivity_ activity;

    @Before
    public void setup() {
        activity = new ResActivity_();
        activity.onCreate(null);
    }
    
    @Test
    public void string_snake_case_injected() {
    	assertThat(activity.injected_string).isEqualTo("test");
    }
    
    @Test
    public void string_camel_case_injected() {
    	assertThat(activity.injectedString).isEqualTo("test");
    }
    

    /**
     * Cannot be tested right now, because there is no Robolectric shadow class
     * for {@link AnimationUtils}.
     */
//     @Test
    public void animNotNull() {
        assertThat(activity.fadein).isNotNull();
    }

    /**
     * Cannot be tested right now, because the Robolectric shadow class for
     * {@link Resources} doesn't implement {@link Resources#getAnimation(int)}
     */
    // @Test
    public void xmlResAnimNotNull() {
        assertThat(activity.fade_in).isNotNull();
    }

}
