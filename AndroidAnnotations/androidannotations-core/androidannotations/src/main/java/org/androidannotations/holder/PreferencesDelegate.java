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

import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JMod.PUBLIC;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.androidannotations.helper.CanonicalNameConstants;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class PreferencesDelegate extends GeneratedClassHolderDelegate<EComponentWithViewSupportHolder> implements HasPreferences {

	protected JBlock addPreferencesFromResourceInjectionBlock;
	protected JBlock addPreferencesFromResourceAfterInjectionBlock;

	private boolean usingSupportV7Preference = false;
	private AbstractJClass basePreferenceClass;

	public PreferencesDelegate(EComponentWithViewSupportHolder holder) {
		super(holder);
		Elements elementUtils = holder.getEnvironment().getProcessingEnvironment().getElementUtils();
		Types typeUtils = holder.getEnvironment().getProcessingEnvironment().getTypeUtils();

		TypeElement supportV7PreferenceFragmentCompat = elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V7_PREFERENCE_FRAGMENTCOMPAT);

		TypeElement supportV14PreferenceFragment = elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V14_PREFERENCE_FRAGMENT);

		TypeMirror annotatedType = holder.getAnnotatedElement().asType();

		if (supportV7PreferenceFragmentCompat != null && typeUtils.isSubtype(annotatedType, supportV7PreferenceFragmentCompat.asType())
				|| supportV14PreferenceFragment != null && typeUtils.isSubtype(annotatedType, supportV14PreferenceFragment.asType())) {
			usingSupportV7Preference = true;
			basePreferenceClass = getClasses().SUPPORT_V7_PREFERENCE;
		} else {
			basePreferenceClass = getClasses().PREFERENCE;
		}
	}

	@Override
	public JBlock getAddPreferencesFromResourceInjectionBlock() {
		if (addPreferencesFromResourceInjectionBlock == null) {
			setAddPreferencesFromResourceBlock();
		}
		return addPreferencesFromResourceInjectionBlock;
	}

	@Override
	public JBlock getAddPreferencesFromResourceAfterInjectionBlock() {
		if (addPreferencesFromResourceAfterInjectionBlock == null) {
			setAddPreferencesFromResourceBlock();
		}
		return addPreferencesFromResourceAfterInjectionBlock;
	}

	private void setAddPreferencesFromResourceBlock() {
		JMethod method = getGeneratedClass().method(PUBLIC, codeModel().VOID, "addPreferencesFromResource");
		method.annotate(Override.class);
		JVar preferencesResIdParam = method.param(int.class, "preferencesResId");
		method.body().invoke(JExpr._super(), "addPreferencesFromResource").arg(preferencesResIdParam);
		addPreferencesFromResourceInjectionBlock = method.body().blockVirtual();
		addPreferencesFromResourceAfterInjectionBlock = method.body().blockVirtual();
	}

	private JInvocation findPreferenceByKey(JFieldRef idRef) {
		JInvocation getString = invoke(_this(), "getString").arg(idRef);
		JInvocation findPreferenceByKey = invoke(_this(), "findPreference");
		return findPreferenceByKey.arg(getString);
	}

	@Override
	public void assignFindPreferenceByKey(JFieldRef idRef, AbstractJClass preferenceClass, JFieldRef fieldRef) {
		String idRefString = idRef.name();
		FoundPreferenceHolder foundViewHolder = (FoundPreferenceHolder) holder.foundHolders.get(idRefString);

		JBlock block = getAddPreferencesFromResourceInjectionBlock();
		IJExpression assignExpression;

		if (foundViewHolder != null) {
			assignExpression = foundViewHolder.getOrCastRef(preferenceClass);
		} else {
			assignExpression = findPreferenceByKey(idRef);
			if (preferenceClass != null && preferenceClass != getClasses().PREFERENCE && preferenceClass != getClasses().SUPPORT_V7_PREFERENCE) {
				assignExpression = cast(preferenceClass, assignExpression);
			}
			holder.foundHolders.put(idRefString, new FoundPreferenceHolder(this, preferenceClass, fieldRef, block));
		}

		block.assign(fieldRef, assignExpression);
	}

	@Override
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, AbstractJClass preferenceClass) {
		String idRefString = idRef.name();
		FoundPreferenceHolder foundPreferenceHolder = (FoundPreferenceHolder) holder.foundHolders.get(idRefString);
		if (foundPreferenceHolder == null) {
			foundPreferenceHolder = createFoundPreferenceAndIfNotNullBlock(idRef, preferenceClass);
			holder.foundHolders.put(idRefString, foundPreferenceHolder);
		}
		return foundPreferenceHolder;
	}

	@Override
	public boolean usingSupportV7Preference() {
		return usingSupportV7Preference;
	}

	@Override
	public AbstractJClass getBasePreferenceClass() {
		return basePreferenceClass;
	}

	private FoundPreferenceHolder createFoundPreferenceAndIfNotNullBlock(JFieldRef idRef, AbstractJClass preferenceClass) {
		IJExpression findPreferenceExpression = findPreferenceByKey(idRef);
		JBlock block = getAddPreferencesFromResourceInjectionBlock().blockSimple();

		if (preferenceClass == null) {
			preferenceClass = basePreferenceClass;
		} else if (!preferenceClass.equals(basePreferenceClass)) {
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
