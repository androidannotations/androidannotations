/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
import com.helger.jcodemodel.IJAssignmentTarget;
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
	private boolean usingAndroidxPreference = false;
	private AbstractJClass basePreferenceClass;

	public PreferencesDelegate(EComponentWithViewSupportHolder holder) {
		super(holder);
		Elements elementUtils = holder.getEnvironment().getProcessingEnvironment().getElementUtils();
		Types typeUtils = holder.getEnvironment().getProcessingEnvironment().getTypeUtils();

		TypeElement supportV7PreferenceFragmentCompat = elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V7_PREFERENCE_FRAGMENTCOMPAT);
		TypeElement andoridxPreferenceFragmentCompat = elementUtils.getTypeElement(CanonicalNameConstants.ANDROIDX_PREFERENCE_FRAGMENTCOMPAT);
		TypeElement supportV14PreferenceFragment = elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V14_PREFERENCE_FRAGMENT);
		TypeElement andoridxPreferenceFragment = elementUtils.getTypeElement(CanonicalNameConstants.ANDROIDX_PREFERENCE_FRAGMENT);

		TypeMirror annotatedType = holder.getAnnotatedElement().asType();

		if (andoridxPreferenceFragmentCompat != null && typeUtils.isSubtype(annotatedType, andoridxPreferenceFragmentCompat.asType())
				|| andoridxPreferenceFragment != null && typeUtils.isSubtype(annotatedType, andoridxPreferenceFragment.asType())) {
			usingAndroidxPreference = true;
			basePreferenceClass = getClasses().ANDROIDX_PREFERENCE;
		} else if (supportV7PreferenceFragmentCompat != null && typeUtils.isSubtype(annotatedType, supportV7PreferenceFragmentCompat.asType())
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
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, AbstractJClass preferenceClass) {
		return getFoundPreferenceHolder(idRef, preferenceClass, null);
	}

	@Override
	public FoundPreferenceHolder getFoundPreferenceHolder(JFieldRef idRef, AbstractJClass preferenceClass, IJAssignmentTarget fieldRef) {
		String idRefString = idRef.name();
		FoundPreferenceHolder foundPreferenceHolder = (FoundPreferenceHolder) holder.foundHolders.get(idRefString);
		if (foundPreferenceHolder == null) {
			foundPreferenceHolder = createFoundPreferenceAndIfNotNullBlock(idRef, preferenceClass, fieldRef);
			holder.foundHolders.put(idRefString, foundPreferenceHolder);
		}
		return foundPreferenceHolder;
	}

	@Override
	public boolean usingSupportV7Preference() {
		return usingSupportV7Preference;
	}

	@Override
	public boolean usingAndroidxPreference() {
		return usingAndroidxPreference;
	}

	@Override
	public AbstractJClass getBasePreferenceClass() {
		return basePreferenceClass;
	}

	private FoundPreferenceHolder createFoundPreferenceAndIfNotNullBlock(JFieldRef idRef, AbstractJClass preferenceClass, IJAssignmentTarget fieldRef) {
		IJExpression findPreferenceExpression = findPreferenceByKey(idRef);
		JBlock block = getAddPreferencesFromResourceInjectionBlock();

		if (preferenceClass == null) {
			preferenceClass = basePreferenceClass;
		} else if (!preferenceClass.equals(basePreferenceClass)) {
			findPreferenceExpression = cast(preferenceClass, findPreferenceExpression);
		}

		IJAssignmentTarget foundPref = fieldRef;
		if (foundPref == null) {
			foundPref = block.decl(preferenceClass, "preference_" + idRef.name(), findPreferenceExpression);
		} else {
			block.add(foundPref.assign(findPreferenceExpression));
		}
		return new FoundPreferenceHolder(this, preferenceClass, foundPref, block);
	}

	@Override
	public JBlock getPreferenceScreenInitializationBlock() {
		// not used
		throw new UnsupportedOperationException();
	}
}
