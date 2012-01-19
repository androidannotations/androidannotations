/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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

import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

public class EViewGroupProcessor extends AnnotationHelper implements ElementProcessor {

	private final IRClass rClass;

	public EViewGroupProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return EViewGroup.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		EBeanHolder holder = eBeansHolder.create(element);

		TypeElement typeElement = (TypeElement) element;

		String eBeanQualifiedName = typeElement.getQualifiedName().toString();

		String generatedBeanQualifiedName = eBeanQualifiedName + ModelConstants.GENERATION_SUFFIX;

		int modifiers;
		if (element.getModifiers().contains(Modifier.ABSTRACT)) {
			modifiers = JMod.PUBLIC | JMod.ABSTRACT;
		} else {
			modifiers = JMod.PUBLIC | JMod.FINAL;
		}

		holder.eBean = codeModel._class(modifiers, generatedBeanQualifiedName, ClassType.CLASS);
		JClass eBeanClass = codeModel.directClass(eBeanQualifiedName);

		holder.eBean._extends(eBeanClass);
		
		holder.eBean.annotate(SuppressWarnings.class).param("value", "unused");
		holder.eBean.javadoc().append( //
				"We use @SuppressWarning here because our java code\n" +
				"generator doesn't know that there is no need\n" +
				"to import OnXXXListeners from View as we already\n" +
				"are in a View. <b>See issue #21.</b>");

		{
			JClass contextClass = holder.refClass("android.content.Context");
			holder.contextRef = holder.eBean.field(PRIVATE, contextClass, "context_");
		}
		
		{
			// init
			holder.init = holder.eBean.method(PRIVATE, codeModel.VOID, "init_");
			holder.init.body().assign((JFieldVar) holder.contextRef, JExpr.invoke("getContext"));
		}
		
		{
			// afterSetContentView
			holder.afterSetContentView = holder.eBean.method(PRIVATE, codeModel.VOID, "afterSetContentView_");
		}
		
		JFieldVar mAlreadyInflated_ = holder.eBean.field(PRIVATE, JType.parse(codeModel, "boolean"), "mAlreadyInflated_", JExpr.FALSE);

		// onFinishInflate
		JMethod onFinishInflate = holder.eBean.method(PUBLIC, codeModel.VOID, "onFinishInflate");
		onFinishInflate.annotate(Override.class);

		JBlock ifNotInflated = onFinishInflate.body()._if(JExpr.ref("mAlreadyInflated_").not())._then();
		ifNotInflated.directStatement("// See issue #68 for details.");
		ifNotInflated.assign(mAlreadyInflated_, JExpr.TRUE);
		
		// inflate layout if ID is given on annotation
		EViewGroup layoutAnnotation = element.getAnnotation(EViewGroup.class);
		int layoutIdValue = layoutAnnotation.value();
		JFieldRef contentViewId;
		if (layoutIdValue != Id.DEFAULT_VALUE) {
			IRInnerClass rInnerClass = rClass.get(Res.LAYOUT);
			contentViewId = rInnerClass.getIdStaticRef(layoutIdValue, holder);
			ifNotInflated.invoke("inflate").arg(JExpr.invoke("getContext")).arg(contentViewId).arg(JExpr._this());
		}
		ifNotInflated.invoke(holder.afterSetContentView);
		
		// finally
		onFinishInflate.body().invoke(JExpr._super(), "onFinishInflate");

		copyConstructors(element, holder, onFinishInflate);
		
		{
			// init if activity
			APTCodeModelHelper helper = new APTCodeModelHelper();
			holder.initIfActivityBody = helper.ifContextInstanceOfActivity(holder, holder.init.body());
			holder.initActivityRef = helper.castContextToActivity(holder, holder.initIfActivityBody);
		}

	}

	private void copyConstructors(Element element, EBeanHolder holder, JMethod setContentViewMethod) {
		List<ExecutableElement> constructors = new ArrayList<ExecutableElement>();
		for (Element e : element.getEnclosedElements()) {
			if (e.getKind() == CONSTRUCTOR) {
				constructors.add((ExecutableElement) e);
			}
		}

		for (ExecutableElement userConstructor : constructors) {
			JMethod copyConstructor = holder.eBean.constructor(PUBLIC);
			JBlock body = copyConstructor.body();
			JInvocation superCall = body.invoke("super");
			for (VariableElement param : userConstructor.getParameters()) {
				String paramName = param.getSimpleName().toString();
				String paramType = param.asType().toString();
				copyConstructor.param(holder.refClass(paramType), paramName);
				superCall.arg(JExpr.ref(paramName));
			}
			
			body.invoke(holder.init);
		}
	}

}
