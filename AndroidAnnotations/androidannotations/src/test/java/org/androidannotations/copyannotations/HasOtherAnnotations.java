/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.copyannotations;

import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Action;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature.Responses;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Trace;

import dalvik.annotation.TestTargetClass;

@EBean
@XmlType
@TestTargetClass(String.class)
@WebServiceRefs(value = { @WebServiceRef(type = String.class) })
public class HasOtherAnnotations {

	@Background
	@Addressing(responses = Responses.ALL)
	@Action(input = "someString")
	@SuppressWarnings(value = { "", "hi" })
	public void onEvent(@Deprecated Event event) {

	}

	@Trace
	@Override
	public String toString() {
		return super.toString();
	}

	protected static class Event {
	}
}
