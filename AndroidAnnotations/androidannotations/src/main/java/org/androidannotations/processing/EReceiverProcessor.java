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

import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.processing.EBeansHolder.Classes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EReceiverProcessor extends GeneratingElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return EReceiver.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder, EBeanHolder holder) throws Exception {

		Classes classes = holder.classes();

		JFieldVar contextField = holder.generatedClass.field(PRIVATE, classes.CONTEXT, "context_");
		holder.contextRef = contextField;

		holder.init = holder.generatedClass.method(PRIVATE, codeModel.VOID, "init_");
		{
			// onReceive
			JMethod onReceive = holder.generatedClass.method(PUBLIC, codeModel.VOID, "onReceive");
			JVar contextParam = onReceive.param(classes.CONTEXT, "context");
			JVar intentParam = onReceive.param(classes.INTENT, "intent");
			onReceive.annotate(Override.class);
			JBlock onReceiveBody = onReceive.body();
			onReceiveBody.assign(contextField, contextParam);
			onReceiveBody.invoke(holder.init);
			onReceiveBody.invoke(JExpr._super(), onReceive).arg(contextParam).arg(intentParam);
		}

		{
			/*
			 * Setting to null shouldn't be a problem as long as we don't allow
			 * 
			 * @App and @Extra on this component
			 */
			holder.initIfActivityBody = null;
			holder.initActivityRef = null;
		}

	}

}
