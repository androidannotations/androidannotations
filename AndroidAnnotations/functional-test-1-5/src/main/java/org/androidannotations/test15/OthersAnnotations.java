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
package org.androidannotations.test15;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import com.test.ComplexAnnotation;
import com.test.SimpleAnnotation;
import dalvik.annotation.TestTargetClass;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Trace;
import org.simpleframework.xml.Attribute;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.meta.When;

@EBean
@Nullable
@TestTargetClass(String.class)
@ComplexAnnotation(value = @SimpleAnnotation("1"), array = {@SimpleAnnotation("2"), @SimpleAnnotation("3")})
public class OthersAnnotations {
	
	@Trace
	@Background
	@SuppressWarnings(value = { "", "hi" })
	@Attribute(name = "2")
	@Nonnull(when = When.MAYBE)
	@Subscribe
	public void onEvent(@Nonnull(when = When.UNKNOWN) Event event) {

	}
	
	@Produce
	@Attribute(name = "2")
	public Event produceEvent() {
		return new Event();
	}

	public static class Event {}
}
