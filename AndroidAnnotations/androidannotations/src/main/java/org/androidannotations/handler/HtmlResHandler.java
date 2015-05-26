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
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;

import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AndroidRes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;

public class HtmlResHandler extends AbstractResHandler {

	public HtmlResHandler(ProcessingEnvironment processingEnvironment) {
		super(AndroidRes.HTML, processingEnvironment);
	}

	@Override
	protected void makeCall(String fieldName, EComponentHolder holder, JBlock methodBody, JFieldRef idRef) {
		methodBody.assign(ref(fieldName), classes().HTML.staticInvoke("fromHtml").arg(holder.getResourcesRef().invoke(AndroidRes.HTML.getResourceMethodName()).arg(idRef)));
	}
}
