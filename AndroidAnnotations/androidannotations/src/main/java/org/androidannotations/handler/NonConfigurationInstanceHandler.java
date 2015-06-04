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

import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;
import static org.androidannotations.helper.ModelConstants.classSuffix;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.holder.NonConfigurationHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JVar;

public class NonConfigurationInstanceHandler extends BaseAnnotationHandler<EActivityHolder> {

	private final AnnotationHelper annotationHelper;

	public NonConfigurationInstanceHandler(ProcessingEnvironment processingEnvironment) {
		super(NonConfigurationInstance.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnv);
		codeModelHelper = new APTCodeModelHelper();
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEActivity(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);
	}

	@Override
	public void process(Element element, EActivityHolder holder) throws JClassAlreadyExistsException {
		String fieldName = element.getSimpleName().toString();
		JClass fieldType = codeModelHelper.typeMirrorToJClass(element.asType(), holder);

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
			JClass fieldGeneratedBeanClass = refClass(typeQualifiedName + classSuffix());

			initIfNonConfigurationNotNullBlock.invoke(cast(fieldGeneratedBeanClass, field), "rebind").arg(_this());
		}
	}
}
