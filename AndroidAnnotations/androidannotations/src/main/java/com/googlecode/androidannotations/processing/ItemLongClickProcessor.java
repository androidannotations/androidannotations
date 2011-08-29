/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.annotations.ItemLongClick;
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
 * @author Benjamin Fellous
 * @author Pierre-Yves Ricau
 */
public class ItemLongClickProcessor implements ElementProcessor {

	private final IRClass rClass;

	public ItemLongClickProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ItemLongClick.class;
	}

    @Override
    public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
        ActivityHolder holder = activitiesHolder.getEnclosingActivityHolder(element);

        String methodName = element.getSimpleName().toString();

        ExecutableElement executableElement = (ExecutableElement) element;
        List<? extends VariableElement> parameters = executableElement.getParameters();
        TypeMirror returnType = executableElement.getReturnType();
        boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;

        boolean hasItemParameter = parameters.size() == 1;
        
        JFieldRef idRef = extractClickQualifiedId(element, holder);

        JDefinedClass onItemClickListenerClass = codeModel.anonymousClass(holder.refClass("android.widget.AdapterView.OnItemLongClickListener"));
        JMethod onItemLongClickMethod = onItemClickListenerClass.method(JMod.PUBLIC, codeModel.BOOLEAN, "onItemLongClick");
        JClass adapterViewClass = holder.refClass("android.widget.AdapterView");
        JClass viewClass = holder.refClass("android.view.View");
        
        JClass narrowAdapterViewClass = adapterViewClass.narrow(codeModel.wildcard());
        JVar onItemClickParentParam = onItemLongClickMethod.param(narrowAdapterViewClass, "parent");
        onItemLongClickMethod.param(viewClass, "view");
        JVar onItemClickPositionParam = onItemLongClickMethod.param(codeModel.INT, "position");
        onItemLongClickMethod.param(codeModel.LONG, "id");
        
        JBlock onItemLongClickBody = onItemLongClickMethod.body();
        
        JInvocation itemClickCall = JExpr.invoke(methodName);
        
        if (returnMethodResult) {
            onItemLongClickBody._return(itemClickCall);
        } else {
            onItemLongClickBody.add(itemClickCall);
            onItemLongClickBody._return(JExpr.TRUE);
        }

        if (hasItemParameter) {
            VariableElement parameter = parameters.get(0);
            String parameterQualifiedName = parameter.asType().toString();
            itemClickCall.arg(JExpr.cast(holder.refClass(parameterQualifiedName), JExpr.invoke(onItemClickParentParam, "getAdapter").invoke("getItem").arg(onItemClickPositionParam)));
        }

        JBlock body = holder.afterSetContentView.body();

        body.add(JExpr.invoke(JExpr.cast(narrowAdapterViewClass, JExpr.invoke("findViewById").arg(idRef)),"setOnItemLongClickListener").arg(JExpr._new(onItemClickListenerClass)));
    }
    
    private JFieldRef extractClickQualifiedId(Element element, ActivityHolder holder) {
        ItemLongClick annotation = element.getAnnotation(ItemLongClick.class);
        int idValue = annotation.value();
        IRInnerClass rInnerClass = rClass.get(Res.ID);
        if (idValue == Id.DEFAULT_VALUE) {
            String fieldName = element.getSimpleName().toString();
            int lastIndex = fieldName.lastIndexOf("ItemLongClicked");
            if (lastIndex != -1) {
                fieldName = fieldName.substring(0, lastIndex);
            }
            return rInnerClass.getIdStaticRef(fieldName, holder);

        } else {
            return rInnerClass.getIdStaticRef(idValue, holder);
        }
    }


}
