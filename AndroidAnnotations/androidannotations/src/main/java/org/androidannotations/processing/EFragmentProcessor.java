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
package org.androidannotations.processing;

import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;
import static com.sun.codemodel.JExpr.FALSE;
import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EFragmentProcessor implements GeneratingElementProcessor {

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

		TypeElement typeElement = (TypeElement) element;

		String beanQualifiedName = typeElement.getQualifiedName().toString();

		String generatedBeanQualifiedName = beanQualifiedName + GENERATION_SUFFIX;

		JDefinedClass generatedClass = codeModel._class(PUBLIC | FINAL, generatedBeanQualifiedName, ClassType.CLASS);

		EBeanHolder holder = eBeansHolder.create(element, getTarget(), generatedClass);

		JClass eBeanClass = codeModel.directClass(beanQualifiedName);

		holder.generatedClass._extends(eBeanClass);

		Classes classes = holder.classes();

		{
			// init
			holder.init = holder.generatedClass.method(PRIVATE, codeModel.VOID, "init_");
			holder.init.param(holder.classes().BUNDLE, "savedInstanceState");
		}

		{
			// onCreate()

			JMethod onCreate = holder.generatedClass.method(PUBLIC, codeModel.VOID, "onCreate");
			onCreate.annotate(Override.class);
			JVar onCreateSavedInstanceState = onCreate.param(classes.BUNDLE, "savedInstanceState");
			JBlock onCreateBody = onCreate.body();

			onCreateBody.invoke(holder.init).arg(onCreateSavedInstanceState);

			onCreateBody.invoke(_super(), onCreate).arg(onCreateSavedInstanceState);
		}

		holder.contextRef = invoke("getActivity");

		// contentView
		JFieldVar contentView = holder.generatedClass.field(PRIVATE, classes.VIEW, "contentView_");

		{
			// afterSetContentView
			holder.afterSetContentView = holder.generatedClass.method(PRIVATE, codeModel.VOID, "afterSetContentView_");
		}

		{
			// onCreateView()
			JMethod onCreateView = holder.generatedClass.method(PUBLIC, classes.VIEW, "onCreateView");
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

			body._return(contentView);
		}

		{
			// onViewCreated

			JMethod onViewCreated = holder.generatedClass.method(PUBLIC, codeModel.VOID, "onViewCreated");
			onViewCreated.annotate(Override.class);
			JVar view = onViewCreated.param(classes.VIEW, "view");
			JVar savedInstanceState = onViewCreated.param(classes.BUNDLE, "savedInstanceState");

			JBlock onViewCreatedBody = onViewCreated.body();

			onViewCreatedBody.invoke(_super(), onViewCreated).arg(view).arg(savedInstanceState);
			
			onViewCreatedBody.invoke(holder.afterSetContentView);
		}


		{
			// findViewById

			JMethod findViewById = holder.generatedClass.method(PUBLIC, classes.VIEW, "findViewById");
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

		addFragmentBuilder(codeModel, holder, eBeanClass);
	}

	private void addFragmentBuilder(JCodeModel codeModel, EBeanHolder holder, JClass eBeanClass) throws JClassAlreadyExistsException {
		JClass bundleClass = holder.classes().BUNDLE;

		{
			holder.fragmentBuilderClass = holder.generatedClass._class(PUBLIC | STATIC, "FragmentBuilder_");
			holder.fragmentArgumentsBuilderField = holder.fragmentBuilderClass.field(PRIVATE, bundleClass, "args_");

			{
				// Constructor
				JMethod constructor = holder.fragmentBuilderClass.constructor(PRIVATE);
				JBlock constructorBody = constructor.body();
				constructorBody.assign(holder.fragmentArgumentsBuilderField, _new(bundleClass));
			}

			{
				// build()
				JMethod method = holder.fragmentBuilderClass.method(PUBLIC, eBeanClass, "build");
				JBlock body = method.body();

				JVar fragment = body.decl(holder.generatedClass, "fragment_", _new(holder.generatedClass));
				body.invoke(fragment, "setArguments").arg(holder.fragmentArgumentsBuilderField);
				body._return(fragment);
			}

			{
				// create()
				JMethod method = holder.generatedClass.method(STATIC | PUBLIC, holder.fragmentBuilderClass, "builder");
				method.body()._return(_new(holder.fragmentBuilderClass));
			}
		}
	}
}
