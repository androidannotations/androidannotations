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

import static com.sun.codemodel.JExpr.lit;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.holder.EComponentWithViewSupportHolder;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;

public class FragmentByTagHandler extends AbstractFragmentByHandler {

	public FragmentByTagHandler(AndroidAnnotationsEnvironment environment) {
		super(FragmentByTag.class, environment);
	}

	@Override
	protected JMethod getFindFragmentMethod(boolean isNativeFragment, EComponentWithViewSupportHolder holder) {
		return isNativeFragment ? holder.getFindNativeFragmentByTag() : holder.getFindSupportFragmentByTag();
	}

	@Override
	protected JExpression getFragmentId(Element element, String fieldName) {
		FragmentByTag annotation = element.getAnnotation(FragmentByTag.class);
		String tagValue = annotation.value();
		if (tagValue.equals("")) {
			tagValue = fieldName;
		}
		return lit(tagValue);
	}
}
