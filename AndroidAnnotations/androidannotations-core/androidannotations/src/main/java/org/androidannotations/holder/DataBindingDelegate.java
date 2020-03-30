/**
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.holder;

import static org.androidannotations.helper.ModelConstants.generationSuffix;

import org.androidannotations.helper.CanonicalNameConstants;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;

class DataBindingDelegate extends GeneratedClassHolderDelegate<EComponentWithViewSupportHolder> {

	private JFieldVar dataBindingField;

	DataBindingDelegate(EComponentWithViewSupportHolder holder) {
		super(holder);
	}

	JFieldVar getDataBindingField() {
		if (dataBindingField == null) {
			setDataBindingField();
		}

		return dataBindingField;
	}

	private void setDataBindingField() {
		AbstractJClass viewDataBinding = getClasses().VIEW_DATA_BINDING;
		if (getEnvironment().getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.ANDROIDX_VIEW_DATA_BINDING) != null) {
			viewDataBinding = getClasses().ANDROIDX_VIEW_DATA_BINDING;
		}
		dataBindingField = holder.generatedClass.field(JMod.PRIVATE, viewDataBinding, "viewDataBinding" + generationSuffix());
	}

	IJExpression getDataBindingInflationExpression(IJExpression contentViewId, IJExpression container, boolean attachToRoot) {
		AbstractJClass dataBindingUtil = getClasses().DATA_BINDING_UTIL;
		if (getEnvironment().getProcessingEnvironment().getElementUtils().getTypeElement(CanonicalNameConstants.ANDROIDX_DATA_BINDING_UTIL) != null) {
			dataBindingUtil = getClasses().ANDROIDX_DATA_BINDING_UTIL;
		}
		return dataBindingUtil.staticInvoke("inflate") //
				.arg(getClasses().LAYOUT_INFLATER.staticInvoke("from").arg(holder.getContextRef())) //
				.arg(contentViewId) //
				.arg(container) //
				.arg(attachToRoot);
	}
}
