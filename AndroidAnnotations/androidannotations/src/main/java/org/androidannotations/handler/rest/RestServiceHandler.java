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
package org.androidannotations.handler.rest;

import static org.androidannotations.helper.ModelConstants.classSuffix;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;

public class RestServiceHandler extends BaseAnnotationHandler<EComponentHolder> {

	private APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public RestServiceHandler(ProcessingEnvironment processingEnvironment) {
		super(RestService.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.typeHasAnnotation(Rest.class, element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror fieldTypeMirror = element.asType();
		TypeMirror erasedFieldTypeMirror = processingEnv.getTypeUtils().erasure(fieldTypeMirror);
		String interfaceName = erasedFieldTypeMirror.toString();

		String generatedClassName = interfaceName + classSuffix();

		JClass clazz = codeModelHelper.narrowGeneratedClass(refClass(generatedClassName), fieldTypeMirror, holder);

		JBlock methodBody = holder.getInitBody();

		JFieldRef field = JExpr.ref(fieldName);

		methodBody.assign(field, JExpr._new(clazz).arg(holder.getContextRef()));
	}
}
