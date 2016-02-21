/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.ref;
import static org.androidannotations.helper.ModelConstants.classSuffix;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.holder.NonConfigurationHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JVar;

public class NonConfigurationInstanceHandler extends BaseAnnotationHandler<EActivityHolder> {

	public NonConfigurationInstanceHandler(AndroidAnnotationsEnvironment environment) {
		super(NonConfigurationInstance.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEActivity(element, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EActivityHolder holder) throws JClassAlreadyExistsException {
		String fieldName = element.getSimpleName().toString();
		AbstractJClass fieldType = codeModelHelper.typeMirrorToJClass(element.asType());

		NonConfigurationHolder ncHolder = holder.getNonConfigurationHolder();
		JFieldVar ncHolderField = ncHolder.createField(fieldName, fieldType);

		injectInInit(element, holder, fieldName, ncHolderField);
		retainInOnRetain(holder, fieldName, ncHolderField);
	}

	private void injectInInit(Element element, EActivityHolder holder, String fieldName, JFieldVar ncHolderField) throws JClassAlreadyExistsException {
		JBlock initIfNonConfigurationNotNullBlock = holder.getInitIfNonConfigurationNotNullBlock();
		JVar initNonConfigurationInstance = holder.getInitNonConfigurationInstance();
		initIfNonConfigurationNotNullBlock.assign(ref(fieldName), initNonConfigurationInstance.ref(ncHolderField));
		rebindContextIfBean(element, initIfNonConfigurationNotNullBlock, ncHolderField);
	}

	private void retainInOnRetain(EActivityHolder holder, String fieldName, JFieldVar ncHolderField) throws JClassAlreadyExistsException {
		JBlock onRetainNonConfigurationInstanceBindBlock = holder.getOnRetainNonConfigurationInstanceBindBlock();
		JVar onRetainNonConfigurationInstance = holder.getOnRetainNonConfigurationInstance();
		onRetainNonConfigurationInstanceBindBlock.assign(onRetainNonConfigurationInstance.ref(ncHolderField), ref(fieldName));
	}

	private void rebindContextIfBean(Element element, JBlock initIfNonConfigurationNotNullBlock, JFieldVar field) {
		boolean hasBeanAnnotation = element.getAnnotation(Bean.class) != null;
		if (hasBeanAnnotation) {

			TypeMirror elementType = annotationHelper.extractAnnotationClassParameter(element, Bean.class.getName());
			if (elementType == null) {
				elementType = element.asType();
			}
			String typeQualifiedName = elementType.toString();
			AbstractJClass fieldGeneratedBeanClass = getJClass(typeQualifiedName + classSuffix());

			// do not generate rebind call for singleton beans
			Element eBeanTypeElement = annotationHelper.getTypeUtils().asElement(elementType);
			EBean eBean = eBeanTypeElement.getAnnotation(EBean.class);
			if (eBean != null && eBean.scope() != EBean.Scope.Singleton) {
				initIfNonConfigurationNotNullBlock.invoke(cast(fieldGeneratedBeanClass, field), "rebind").arg(_this());
			}
		}
	}
}
