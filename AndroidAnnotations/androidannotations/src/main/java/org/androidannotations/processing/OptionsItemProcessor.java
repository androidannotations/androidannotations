/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.processing;

import static com.sun.codemodel.JExpr.FALSE;
import static com.sun.codemodel.JExpr.TRUE;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.ThirdPartyLibHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 */
public class OptionsItemProcessor implements DecoratingElementProcessor {

	private final IdAnnotationHelper helper;

	private final ThirdPartyLibHelper libHelper;

	public OptionsItemProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		helper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		libHelper = new ThirdPartyLibHelper(helper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return OptionsItem.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String methodName = element.getSimpleName().toString();

		JClass menuItemClass;
		if (libHelper.usesActionBarSherlock(holder)) {
			menuItemClass = classes.SHERLOCK_MENU_ITEM;
		} else {
			menuItemClass = classes.MENU_ITEM;
		}

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		TypeMirror returnType = executableElement.getReturnType();
		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;

		boolean hasItemParameter = parameters.size() == 1;

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		if (holder.onOptionsItemSelectedIfElseBlock == null) {
			JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.BOOLEAN, "onOptionsItemSelected");
			method.annotate(Override.class);
			holder.onOptionsItemSelectedItem = method.param(menuItemClass, "item");

			JBlock body = method.body();
			JVar handled = body.decl(codeModel.BOOLEAN, "handled", invoke(_super(), method).arg(holder.onOptionsItemSelectedItem));

			body._if(handled)._then()._return(TRUE);

			holder.onOptionsItemSelectedItemId = body.decl(codeModel.INT, "itemId_", holder.onOptionsItemSelectedItem.invoke("getItemId"));
			holder.onOptionsItemSelectedIfElseBlock = body.block();

			body._return(FALSE);
		}

		JExpression ifExpr = holder.onOptionsItemSelectedItemId.eq(idsRefs.get(0));

		for (int i = 1; i < idsRefs.size(); i++) {
			ifExpr = ifExpr.cor(holder.onOptionsItemSelectedItemId.eq(idsRefs.get(i)));
		}

		JBlock itemIfBody = holder.onOptionsItemSelectedIfElseBlock._if(ifExpr)._then();
		JInvocation methodCall = invoke(methodName);

		if (returnMethodResult) {
			itemIfBody._return(methodCall);
		} else {
			itemIfBody.add(methodCall);
			itemIfBody._return(TRUE);
		}

		if (hasItemParameter) {
			methodCall.arg(holder.onOptionsItemSelectedItem);
		}

	}

}
