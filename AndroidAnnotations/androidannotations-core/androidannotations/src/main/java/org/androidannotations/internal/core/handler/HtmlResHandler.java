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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr.ref;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.internal.core.model.AndroidRes;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;

public class HtmlResHandler extends AbstractResHandler {

	public HtmlResHandler(AndroidAnnotationsEnvironment environment) {
		super(AndroidRes.HTML, environment);
	}

	@Override
	protected void makeCall(String fieldName, EComponentHolder holder, JBlock methodBody, JFieldRef idRef) {
		methodBody.assign(ref(fieldName), getClasses().HTML.staticInvoke("fromHtml").arg(holder.getResourcesRef().invoke(AndroidRes.HTML.getResourceMethodName()).arg(idRef)));
	}
}
