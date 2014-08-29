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
package org.androidannotations.holder;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.androidannotations.helper.ModelConstants;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;

public class ReceiverRegistrationHolder {

	private EComponentHolder holder;
	private Map<IntentFilterData, JFieldVar> intentFilterFields = new HashMap<IntentFilterData, JFieldVar>();
	private IllegalStateException illegalStateException = new IllegalStateException("This shouldn't happen unless the validation is bad");

	public ReceiverRegistrationHolder(EComponentHolder holder) {
		this.holder = holder;
	}

	public JFieldVar getIntentFilterField(String[] actions, String[] dataSchemes) {
		IntentFilterData intentFilterData = new IntentFilterData(actions, dataSchemes);
		JFieldVar intentFilterField = intentFilterFields.get(intentFilterData);
		if (intentFilterField == null) {
			intentFilterField = createIntentFilterField(intentFilterData);
			intentFilterFields.put(intentFilterData, intentFilterField);
		}
		return intentFilterField;
	}

	private JFieldVar createIntentFilterField(IntentFilterData intentFilterData) {
		String intentFilterName = "intentFilter" + (intentFilterFields.size() + 1) + ModelConstants.GENERATION_SUFFIX;
		JExpression newIntentFilterExpr = _new(classes().INTENT_FILTER);
		JFieldVar intentFilterField = getGeneratedClass().field(PRIVATE | FINAL, classes().INTENT_FILTER, intentFilterName, newIntentFilterExpr);

		JBlock initBody = holder.getInitBody();
		for (String action : intentFilterData.getActionSet()) {
			initBody.invoke(intentFilterField, "addAction").arg(action);
		}
		for (String dataScheme : intentFilterData.getDataSchemeSet()) {
			initBody.invoke(intentFilterField, "addDataScheme").arg(dataScheme);
		}

		return intentFilterField;
	}

	public JBlock getOnStartAfterSuperBlock() {
		throw illegalStateException;
	}

	public JBlock getOnStopBeforeSuperBlock() {
		throw illegalStateException;
	}

	public JBlock getOnPauseBeforeSuperBlock() {
		throw illegalStateException;
	}

	public JBlock getOnAttachAfterSuperBlock() {
		throw illegalStateException;
	}

	public JBlock getOnDetachBeforeSuperBlock() {
		throw illegalStateException;
	}

	public JDefinedClass getGeneratedClass() {
		return holder.getGeneratedClass();
	}

	public ProcessHolder.Classes classes() {
		return holder.classes();
	}

	private static class IntentFilterData {

		private final Set<String> actionSet;
		private final Set<String> dataSchemeSet;

		public IntentFilterData(String[] actions, String[] dataSchemes) {
			actionSet = new HashSet<String>(Arrays.asList(actions));
			dataSchemeSet = new HashSet<String>(Arrays.asList(dataSchemes));
		}

		public Set<String> getActionSet() {
			return actionSet;
		}

		public Set<String> getDataSchemeSet() {
			return dataSchemeSet;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (actionSet == null ? 0 : actionSet.hashCode());
			result = prime * result + (dataSchemeSet == null ? 0 : dataSchemeSet.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			IntentFilterData other = (IntentFilterData) obj;
			if (actionSet == null) {
				if (other.actionSet != null) {
					return false;
				}
			} else if (!actionSet.equals(other.actionSet)) {
				return false;
			}
			if (dataSchemeSet == null) {
				if (other.dataSchemeSet != null) {
					return false;
				}
			} else if (!dataSchemeSet.equals(other.dataSchemeSet)) {
				return false;
			}
			return true;
		}
	}
}
