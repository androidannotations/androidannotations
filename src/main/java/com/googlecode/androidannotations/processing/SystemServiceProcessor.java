/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.generation.SystemServiceInstruction;
import com.googlecode.androidannotations.model.AndroidSystemServices;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;

public class SystemServiceProcessor implements ElementProcessor {

    private final AndroidSystemServices androidSystemServices;

    public SystemServiceProcessor(AndroidSystemServices androidSystemServices) {
        this.androidSystemServices = androidSystemServices;
    }

    @Override
    public Class<? extends Annotation> getTarget() {
        return SystemService.class;
    }

    @Override
    public void process(Element element, MetaModel metaModel) {
        String fieldName = element.getSimpleName().toString();

        TypeMirror serviceType = element.asType();
        String fieldTypeQualifiedName = serviceType.toString();

        String serviceConstant = androidSystemServices.getServiceConstant(serviceType);

        Element enclosingElement = element.getEnclosingElement();
        MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
        List<Instruction> beforeCreateInstructions = metaActivity.getBeforeCreateInstructions();

        Instruction instruction = new SystemServiceInstruction(fieldName, fieldTypeQualifiedName, serviceConstant);
        beforeCreateInstructions.add(instruction);

    }

    @Override
    public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {

        ActivityHolder holder = activitiesHolder.getActivityHolder(element);

        String fieldName = element.getSimpleName().toString();

        TypeMirror serviceType = element.asType();
        String fieldTypeQualifiedName = serviceType.toString();

        JFieldRef serviceRef = androidSystemServices.getServiceConstant(serviceType, holder);

        JBlock methodBody = holder.beforeSetContentView.body();

        methodBody.assign(JExpr.ref(fieldName), JExpr.cast(holder.refClass(fieldTypeQualifiedName), JExpr.invoke("getSystemService").arg(serviceRef)));
    }

}
