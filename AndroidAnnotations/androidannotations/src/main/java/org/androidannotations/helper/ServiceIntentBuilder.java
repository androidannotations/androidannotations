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
package org.androidannotations.helper;

import static com.sun.codemodel.JMod.PUBLIC;

import org.androidannotations.holder.HasIntentBuilder;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JMethod;

public class ServiceIntentBuilder extends IntentBuilder {

	public ServiceIntentBuilder(HasIntentBuilder holder, AndroidManifest androidManifest) {
		super(holder, androidManifest);
	}

	@Override
	public void build() throws JClassAlreadyExistsException {
		super.build();
		createStart();
		createStop();
	}

	private void createStart() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.classes().COMPONENT_NAME, "start");
		method.body()._return(contextField.invoke("startService").arg(holder.getIntentField()));
	}

	private void createStop() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.codeModel().BOOLEAN, "stop");
		method.body()._return(contextField.invoke("stopService").arg(holder.getIntentField()));
	}

}
