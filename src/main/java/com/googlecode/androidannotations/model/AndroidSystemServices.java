/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.model;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

public class AndroidSystemServices {

	private Map<String, String> registeredServices = new HashMap<String, String>();

	public AndroidSystemServices() {
		registeredServices.put("android.app.NotificationManager", "android.content.Context.NOTIFICATION_SERVICE");
	}

	public boolean contains(TypeMirror serviceType) {
		return registeredServices.containsKey(serviceType.toString());
	}

	public String getServiceConstant(TypeMirror serviceType) {
		return registeredServices.get(serviceType.toString());
	}

}
