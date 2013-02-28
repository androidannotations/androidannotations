/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.test15;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidAnnotationsTestRunner.class)
public class TracedActivityTest {

    @Test
    public void servicesAreInjected() throws IOException {
    	TracedActivity_ activity = new TracedActivity_();
    	activity.onCreate(null);
    	
    	assertThat(activity.tracedMethodCalled).isFalse();
    	activity.tracedMethod(null, null);
    	assertThat(activity.tracedMethodCalled).isTrue();

    	assertThat(activity.voidTracedMethodCalled).isFalse();
    	activity.voidTracedMethod(null, null);
    	assertThat(activity.voidTracedMethodCalled).isTrue();

    	assertThat(activity.voidTracedMethodDebugCalled).isFalse();
    	activity.voidTracedMethodDebug();
    	assertThat(activity.voidTracedMethodDebugCalled).isTrue();

    	assertThat(activity.voidTracedMethodErrorCalled).isFalse();
    	activity.voidTracedMethodError();
    	assertThat(activity.voidTracedMethodErrorCalled).isTrue();

    	assertThat(activity.voidTracedMethodInfoCalled).isFalse();
    	activity.voidTracedMethodInfo();
    	assertThat(activity.voidTracedMethodInfoCalled).isTrue();

    	assertThat(activity.voidTracedMethodVerboseCalled).isFalse();
    	activity.voidTracedMethodVerbose();
    	assertThat(activity.voidTracedMethodVerboseCalled).isTrue();

    	assertThat(activity.voidTracedMethodWarnCalled).isFalse();
    	activity.voidTracedMethodWarn();
    	assertThat(activity.voidTracedMethodWarnCalled).isTrue();

        assertThat(activity.overloadedMethodInt).isFalse();
        activity.overloadedMethod(0);
        assertThat(activity.overloadedMethodInt).isTrue();

        assertThat(activity.overloadedMethodIntFLoat).isFalse();
        activity.overloadedMethod(0, 0f);
        assertThat(activity.overloadedMethodIntFLoat).isTrue();
    }
    
    
}
