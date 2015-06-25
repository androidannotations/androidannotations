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
package org.androidannotations.test15.otto;

import org.androidannotations.annotations.EActivity;

import android.app.Activity;
import android.os.Bundle;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

@EActivity
public class OttoActivity extends Activity {

	Event lastEvent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bus bus = new Bus();
		bus.register(this);
	}
	
	@Subscribe
	public void onEvent(Event event) {
		lastEvent = event;
	}
	
	@Produce
	public Event produceEvent() {
		return new Event();
	}
}
