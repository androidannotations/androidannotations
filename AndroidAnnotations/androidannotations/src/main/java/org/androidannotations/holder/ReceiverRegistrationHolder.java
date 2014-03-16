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

import com.sun.codemodel.*;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.process.ProcessHolder;

import java.util.*;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;

public class ReceiverRegistrationHolder {

	private EComponentHolder holder;
	private Map<Set<String>, JFieldVar> intentFilterFields = new HashMap<Set<String>, JFieldVar>();
	private IllegalStateException illegalStateException = new IllegalStateException("This shouldn't happen unless the validation is bad");

	public ReceiverRegistrationHolder(EComponentHolder holder) {
		this.holder = holder;
	}

	public JFieldVar getIntentFilterField(String[] actions) {
		Arrays.sort(actions);
		Set<String> actionSet = new HashSet<String>(Arrays.asList(actions));
		JFieldVar intentFilterField = intentFilterFields.get(actionSet);
		if (intentFilterField == null) {
			intentFilterField = createIntentFilterField(actionSet);
			intentFilterFields.put(actionSet, intentFilterField);
		}
		return intentFilterField;
	}

	private JFieldVar createIntentFilterField(Set<String> actionSet) {
		String intentFilterName = "intentFilter"+(intentFilterFields.size()+1)+ ModelConstants.GENERATION_SUFFIX;
		JExpression newIntentFilterExpr = _new(classes().INTENT_FILTER);
		JFieldVar intentFilterField = getGeneratedClass().field(PRIVATE | FINAL, classes().INTENT_FILTER, intentFilterName, newIntentFilterExpr);

		JBlock initBody = holder.getInitBody();
		for(String action : actionSet) {
			initBody.invoke(intentFilterField, "addAction").arg(action);
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
}
