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
package org.androidannotations.handler.rest;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;


public class RestServiceHandler extends BaseAnnotationHandler<EComponentHolder> {

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
        String interfaceName = fieldTypeMirror.toString();

        String generatedClassName = interfaceName + ModelConstants.GENERATION_SUFFIX;

        JBlock methodBody = holder.getInitBody();

        JFieldRef field = JExpr.ref(fieldName);

        methodBody.assign(field, JExpr._new(refClass(generatedClassName)).arg(holder.getContextRef()));
    }
}
