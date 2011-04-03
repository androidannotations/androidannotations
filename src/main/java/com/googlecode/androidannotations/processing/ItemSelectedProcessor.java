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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.annotations.ItemSelect;
import com.googlecode.androidannotations.generation.ItemSelectedInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * @author Pierre-Yves Ricau
 */
public class ItemSelectedProcessor implements ElementProcessor {

    private final IRClass rClass;

    public ItemSelectedProcessor(IRClass rClass) {
        this.rClass = rClass;
    }

    @Override
    public Class<? extends Annotation> getTarget() {
        return ItemSelect.class;
    }

    @Override
    public void process(Element element, MetaModel metaModel) {

        String methodName = element.getSimpleName().toString();

        ItemSelect annotation = element.getAnnotation(ItemSelect.class);

        int idValue = annotation.value();

        IRInnerClass rInnerClass = rClass.get(Res.ID);
        String itemClickQualifiedId;

        if (idValue == Id.DEFAULT_VALUE) {
            String fieldName = element.getSimpleName().toString();
            int lastIndex = fieldName.lastIndexOf("ItemSelected");
            if (lastIndex != -1) {
                fieldName = fieldName.substring(0, lastIndex);
            }
            itemClickQualifiedId = rInnerClass.getIdQualifiedName(fieldName);
        } else {
            itemClickQualifiedId = rInnerClass.getIdQualifiedName(idValue);
        }

        Element enclosingElement = element.getEnclosingElement();
        MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
        List<Instruction> onCreateInstructions = metaActivity.getOnCreateInstructions();

        ExecutableElement executableElement = (ExecutableElement) element;
        List<? extends VariableElement> parameters = executableElement.getParameters();

        Instruction instruction;
        if (parameters.size() == 2) {
            VariableElement parameter = parameters.get(1);
            String parameterQualifiedName = parameter.asType().toString();
            instruction = new ItemSelectedInstruction(methodName, itemClickQualifiedId, parameterQualifiedName);
        } else {
            instruction = new ItemSelectedInstruction(methodName, itemClickQualifiedId);
        }
        onCreateInstructions.add(instruction);

    }

    @Override
    public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
        ActivityHolder holder = activitiesHolder.getEnclosingActivityHolder(element);

        String methodName = element.getSimpleName().toString();

        ExecutableElement executableElement = (ExecutableElement) element;
        List<? extends VariableElement> parameters = executableElement.getParameters();

        boolean hasItemParameter = parameters.size() == 2;

        JFieldRef idRef = extractClickQualifiedId(element, holder);

        JDefinedClass onItemSelectedListenerClass = codeModel.anonymousClass(holder.refClass("android.widget.AdapterView.OnItemSelectedListener"));
        JMethod onItemSelectedMethod = onItemSelectedListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onItemSelected");
        JClass adapterViewClass = holder.refClass("android.widget.AdapterView");
        JClass viewClass = holder.refClass("android.view.View");

        JClass narrowAdapterViewClass = adapterViewClass.narrow(codeModel.wildcard());
        JVar onItemClickParentParam = onItemSelectedMethod.param(narrowAdapterViewClass, "parent");
        onItemSelectedMethod.param(viewClass, "view");
        JVar onItemClickPositionParam = onItemSelectedMethod.param(codeModel.INT, "position");
        onItemSelectedMethod.param(codeModel.LONG, "id");

        JInvocation itemSelectedCall = onItemSelectedMethod.body().invoke(methodName);

        itemSelectedCall.arg(JExpr.TRUE);

        if (hasItemParameter) {
            VariableElement parameter = parameters.get(1);
            String parameterQualifiedName = parameter.asType().toString();
            itemSelectedCall.arg(JExpr.cast(holder.refClass(parameterQualifiedName), JExpr.invoke(onItemClickParentParam, "getAdapter").invoke("getItem").arg(onItemClickPositionParam)));
        }

        JMethod onNothingSelectedMethod = onItemSelectedListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onNothingSelected");

        onNothingSelectedMethod.param(narrowAdapterViewClass, "parent");

        JInvocation nothingSelectedCall = onNothingSelectedMethod.body().invoke(methodName);

        nothingSelectedCall.arg(JExpr.FALSE);
        if (hasItemParameter) {
            nothingSelectedCall.arg(JExpr._null());
        }

        JBlock body = holder.afterSetContentView.body();

        JInvocation findViewById = JExpr.invoke("findViewById");
        body.add(JExpr.invoke(JExpr.cast(narrowAdapterViewClass, findViewById.arg(idRef)), "setOnItemSelectedListener").arg(JExpr._new(onItemSelectedListenerClass)));
    }

    private JFieldRef extractClickQualifiedId(Element element, ActivityHolder holder) {
        ItemSelect annotation = element.getAnnotation(ItemSelect.class);
        int idValue = annotation.value();
        IRInnerClass rInnerClass = rClass.get(Res.ID);
        if (idValue == Id.DEFAULT_VALUE) {
            String fieldName = element.getSimpleName().toString();
            int lastIndex = fieldName.lastIndexOf("ItemSelected");
            if (lastIndex != -1) {
                fieldName = fieldName.substring(0, lastIndex);
            }
            return rInnerClass.getIdStaticRef(fieldName, holder);

        } else {
            return rInnerClass.getIdStaticRef(idValue, holder);
        }
    }

}
