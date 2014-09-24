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

import static com.sun.codemodel.JExpr._new;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.holder.FoundPreferenceHolder;
import org.androidannotations.holder.HasPreferences;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldRef;

public abstract class AbstractPreferenceListenerHandler extends AbstractListenerHandler<HasPreferences> {

	public AbstractPreferenceListenerHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		super(targetClass, processingEnvironment);
	}

	public AbstractPreferenceListenerHandler(String target, ProcessingEnvironment processingEnvironment) {
		super(target, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validatedElements, valid);
	}

	@Override
	protected final JClass getListenerTargetClass() {
		return classes().PREFERENCE;
	}

	@Override
	protected final Res getResourceType() {
		return Res.STRING;
	}

	@Override
	protected final void assignListeners(HasPreferences holder, List<JFieldRef> idsRefs, JDefinedClass listenerAnonymousClass) {
		for (JFieldRef idRef : idsRefs) {
			FoundPreferenceHolder foundPreferenceHolder = holder.getFoundPreferenceHolder(idRef, getListenerTargetClass());
			foundPreferenceHolder.getIfNotNullBlock().invoke(foundPreferenceHolder.getRef(), getSetterName()).arg(_new(listenerAnonymousClass));
		}
	}

}
