/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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

import static com.googlecode.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;
import static com.sun.codemodel.JExpr.FALSE;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.helper.IdAnnotationHelper;
import com.googlecode.androidannotations.processing.EBeansHolder.Classes;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EFragmentProcessor implements ElementProcessor {

	private final IdAnnotationHelper helper;

	public EFragmentProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		helper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return EFragment.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		EBeanHolder holder = eBeansHolder.create(element, getTarget());

		TypeElement typeElement = (TypeElement) element;

		String beanQualifiedName = typeElement.getQualifiedName().toString();

		String generatedBeanQualifiedName = beanQualifiedName + GENERATION_SUFFIX;

		holder.eBean = codeModel._class(PUBLIC | FINAL, generatedBeanQualifiedName, ClassType.CLASS);

		JClass eBeanClass = codeModel.directClass(beanQualifiedName);

		holder.eBean._extends(eBeanClass);

		Classes classes = holder.classes();

		{
			// init
			holder.init = holder.eBean.method(PRIVATE, codeModel.VOID, "init_");
			holder.init.param(holder.classes().BUNDLE, "savedInstanceState");
		}

		{
			// onCreate()

			JMethod onCreate = holder.eBean.method(PUBLIC, codeModel.VOID, "onCreate");
			onCreate.annotate(Override.class);
			JVar onCreateSavedInstanceState = onCreate.param(classes.BUNDLE, "savedInstanceState");
			JBlock onCreateBody = onCreate.body();

			onCreateBody.invoke(holder.init).arg(onCreateSavedInstanceState);

			onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);
		}

		holder.contextRef = invoke("getActivity");

		// contentView
		JFieldVar contentView = holder.eBean.field(PRIVATE, classes.VIEW, "contentView_");

		{
			// afterSetContentView
			holder.afterSetContentView = holder.eBean.method(PRIVATE, codeModel.VOID, "afterSetContentView_");
		}

		{
			// onCreateView()
			JMethod onCreateView = holder.eBean.method(PUBLIC, classes.VIEW, "onCreateView");
			onCreateView.annotate(Override.class);
			JVar inflater = onCreateView.param(classes.LAYOUT_INFLATER, "inflater");
			JVar container = onCreateView.param(classes.VIEW_GROUP, "container");
			JVar savedInstanceState = onCreateView.param(classes.BUNDLE, "savedInstanceState");

			JBlock body = onCreateView.body();
			body.assign(contentView, _super().invoke(onCreateView).arg(inflater).arg(container).arg(savedInstanceState));

			JFieldRef contentViewId = helper.extractOneAnnotationFieldRef(holder, element, Res.LAYOUT, false);

			if (contentViewId != null) {
				body._if(contentView.eq(_null())) //
						._then() //
						.assign(contentView, inflater.invoke("inflate").arg(contentViewId).arg(container).arg(FALSE));
			}

			body.invoke(holder.afterSetContentView);

			body._return(contentView);
		}

		{
			// findViewById

			JMethod findViewById = holder.eBean.method(PUBLIC, classes.VIEW, "findViewById");
			JVar idParam = findViewById.param(codeModel.INT, "id");

			JBlock body = findViewById.body();

			body._if(contentView.eq(_null())) //
					._then()._return(_null());

			body._return(contentView.invoke(findViewById).arg(idParam));
		}

		{
			// init if activity
			holder.initIfActivityBody = holder.init.body();
			holder.initActivityRef = holder.contextRef;
		}

	}
}
