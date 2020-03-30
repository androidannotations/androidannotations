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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr._new;

import java.util.List;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.FoundViewHolder;
import org.androidannotations.rclass.IRClass.Res;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldRef;

public abstract class AbstractViewListenerHandler extends AbstractListenerHandler<EComponentWithViewSupportHolder> {

	public AbstractViewListenerHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		super(targetClass, environment);
	}

	public AbstractViewListenerHandler(String target, AndroidAnnotationsEnvironment environment) {
		super(target, environment);
	}

	@Override
	public void validate(Element element, ElementValidation valid) {
		super.validate(element, valid);
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, valid);
	}

	@Override
	protected final void assignListeners(EComponentWithViewSupportHolder holder, List<JFieldRef> idsRefs, JDefinedClass listenerAnonymousClass) {
		for (JFieldRef idRef : idsRefs) {
			AbstractJClass listenerTargetClass = getListenerTargetClass(holder);
			FoundViewHolder foundViewHolder = holder.getFoundViewHolder(idRef, listenerTargetClass);
			foundViewHolder.getIfNotNullBlock().invoke(foundViewHolder.getOrCastRef(listenerTargetClass), getSetterName()).arg(_new(listenerAnonymousClass));
		}
	}

	@Override
	protected AbstractJClass getListenerTargetClass(EComponentWithViewSupportHolder holder) {
		return getClasses().VIEW;
	}

	@Override
	protected final Res getResourceType() {
		return Res.ID;
	}

}
