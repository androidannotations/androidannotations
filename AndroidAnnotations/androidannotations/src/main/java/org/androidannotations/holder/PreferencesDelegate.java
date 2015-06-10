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
package org.androidannotations.holder;

import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PUBLIC;

import org.androidannotations.helper.APTCodeModelHelper;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class PreferencesDelegate extends GeneratedClassHolderDelegate<EComponentWithViewSupportHolder> implements HasPreferences {

	private APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	protected JBlock addPreferencesFromResourceBlock;

	public PreferencesDelegate(EComponentWithViewSupportHolder holder) {
		super(holder);
	}

	@Override
	public JBlock getAddPreferencesFromResourceBlock() {
		if (addPreferencesFromResourceBlock == null) {
			setAddPreferencesFromResourceBlock();
		}
		return addPreferencesFromResourceBlock;
	}

	private void setAddPreferencesFromResourceBlock() {
		JMethod method = getGeneratedClass().method(PUBLIC, codeModel().VOID, "addPreferencesFromResource");
		method.annotate(Override.class);
		JVar preferencesResIdParam = method.param(int.class, "preferencesResId");
		method.body().invoke(JExpr._super(), "addPreferencesFromResource").arg(preferencesResIdParam);
		addPreferencesFromResourceBlock = method.body();
	}

	private JInvocation findPreferenceByKey(JFieldRef idRef) {
		JInvocation getString = invoke(_this(), "getString").arg(idRef);
		JInvocation findPreferenceByKey = invoke(_this(), "findPreference");
		return findPreferenceByKey.arg(getString);
	}

	@Override
	public void assignFindPreferenceByKey(JFieldRef idRef, JClass preferenceClass, JFieldRef fieldRef) {
		String idRefString = codeModelHelper.getIdStringFromIdFieldRef(idRef);
		FoundPreferenceHolder foundViewHolder = (FoundPreferenceHolder) holder.foundHolders.get(idRefString);

		JBlock block = getAddPreferencesFromResourceBlock();
		JExpression assignExpression;

		if (foundViewHolder != null) {
			assignExpression = foundViewHolder.getOrCastRef(preferenceClass);
		} else {
			assignExpression = findPreferenceByKey(idRef);
			if (preferenceClass != null && preferenceClass != classes().PREFERENCE) {
				assignExpression = cast(preferenceClass, assignExpression);
			}
			holder.foundHolders.put(idRefString, new FoundPreferenceHolder(this, preferenceClass, fieldRef, block));
		}

		block.assign(fieldRef, assignExpression);
	}

	@Override
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, JClass preferenceClass) {
		String idRefString = codeModelHelper.getIdStringFromIdFieldRef(idRef);
		FoundPreferenceHolder foundPreferenceHolder = (FoundPreferenceHolder) holder.foundHolders.get(idRefString);
		if (foundPreferenceHolder == null) {
			foundPreferenceHolder = createFoundPreferenceAndIfNotNullBlock(idRef, preferenceClass);
			holder.foundHolders.put(idRefString, foundPreferenceHolder);
		}
		return foundPreferenceHolder;
	}

	private FoundPreferenceHolder createFoundPreferenceAndIfNotNullBlock(JFieldRef idRef, JClass preferenceClass) {
		JExpression findPreferenceExpression = findPreferenceByKey(idRef);
		JBlock block = getAddPreferencesFromResourceBlock().block();

		if (preferenceClass == null) {
			preferenceClass = classes().PREFERENCE;
		} else if (preferenceClass != classes().PREFERENCE) {
			findPreferenceExpression = cast(preferenceClass, findPreferenceExpression);
		}

		JVar preference = block.decl(preferenceClass, "preference", findPreferenceExpression);
		return new FoundPreferenceHolder(this, preferenceClass, preference, block);
	}

	@Override
	public JBlock getPreferenceScreenInitializationBlock() {
		// not used
		throw new UnsupportedOperationException();
	}
}
