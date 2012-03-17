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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class NonConfigurationInstanceProcessor implements ElementProcessor {

	private APTCodeModelHelper aptCodeModelHelper;

	public NonConfigurationInstanceProcessor() {
		aptCodeModelHelper = new APTCodeModelHelper();
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return NonConfigurationInstance.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws JClassAlreadyExistsException {
		EBeanHolder holder = activitiesHolder.getEnclosingEBeanHolder(element);

		NonConfigurationHolder ncHolder = holder.nonConfigurationHolder;

		if (ncHolder == null) {

			ncHolder = new NonConfigurationHolder();
			holder.nonConfigurationHolder = ncHolder;

			ncHolder.holderClass = holder.eBean._class(JMod.PRIVATE | JMod.STATIC, "NonConfigurationInstancesHolder");

			JFieldVar superNonConfigurationInstanceField = ncHolder.holderClass.field(PUBLIC | FINAL, Object.class, "superNonConfigurationInstance");

			ncHolder.holderConstructor = ncHolder.holderClass.constructor(PUBLIC);

			JVar superNonConfigurationInstanceParam = ncHolder.holderConstructor.param(Object.class, "superNonConfigurationInstance");

			ncHolder.holderConstructor.body() //
					.assign(_this().ref(superNonConfigurationInstanceField), superNonConfigurationInstanceParam);

			{
				// init()
				JBlock initBody = holder.init.body();
				ncHolder.initNonConfigurationInstance = initBody.decl(ncHolder.holderClass, "nonConfigurationInstance", cast(ncHolder.holderClass, _super().invoke("getLastNonConfigurationInstance")));
				ncHolder.initIfNonConfiguration = initBody._if(ncHolder.initNonConfigurationInstance.ne(_null()))._then();
			}

			{
				// getLastNonConfigurationInstance()
				JMethod getLastNonConfigurationInstance = holder.eBean.method(PUBLIC, Object.class, "getLastNonConfigurationInstance");
				getLastNonConfigurationInstance.annotate(Override.class);
				JBlock body = getLastNonConfigurationInstance.body();

				JVar nonConfigurationInstance = body.decl(ncHolder.holderClass, "nonConfigurationInstance", cast(ncHolder.holderClass, _super().invoke(getLastNonConfigurationInstance)));

				body._if(nonConfigurationInstance.eq(_null()))._then()._return(_null());

				body._return(nonConfigurationInstance.ref(superNonConfigurationInstanceField));
			}

			{
				// onRetainNonConfigurationInstance()
				JMethod onRetainNonConfigurationInstance = holder.eBean.method(PUBLIC, ncHolder.holderClass, "onRetainNonConfigurationInstance");
				onRetainNonConfigurationInstance.annotate(Override.class);
				ncHolder.newHolder = _new(ncHolder.holderClass);
				ncHolder.newHolder.arg(_super().invoke(onRetainNonConfigurationInstance));
				onRetainNonConfigurationInstance.body()._return(ncHolder.newHolder);
			}
		}

		String fieldName = element.getSimpleName().toString();
		JClass fieldType = aptCodeModelHelper.typeMirrorToJClass(element.asType(), holder);
		JFieldVar field = ncHolder.holderClass.field(PUBLIC | FINAL, fieldType, fieldName);

		JVar constructorParam = ncHolder.holderConstructor.param(fieldType, fieldName);

		ncHolder.holderConstructor.body() //
				.assign(_this().ref(field), constructorParam);

		ncHolder.newHolder.arg(field);

		ncHolder.initIfNonConfiguration.assign(field, ncHolder.initNonConfigurationInstance.ref(field));

		boolean hasBeanAnnotation = element.getAnnotation(Bean.class) != null;
		if (hasBeanAnnotation) {
			JClass fieldGeneratedBeanClass = holder.refClass(fieldType.fullName() + "_");

			ncHolder.initIfNonConfiguration.invoke(cast(fieldGeneratedBeanClass, field), "rebind").arg(_this());
		}

	}

}