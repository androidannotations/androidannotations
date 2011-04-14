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

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.Extra;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class ExtraProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return Extra.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
		Extra annotation = element.getAnnotation(Extra.class);
		String extraKey = annotation.value();
		String fieldName = element.getSimpleName().toString();
		ActivityHolder holder = activitiesHolder.getEnclosingActivityHolder(element);
		JClass fieldType = holder.refClass(element.asType().toString());


		JBlock methodBody = holder.beforeSetContentView.body();

		if (holder.extras == null) {
			holder.extras = methodBody.decl(holder.bundleClass, "extras_");
			holder.extras.init(JExpr.invoke("getIntent").invoke("getExtras"));

			holder.extrasNotNullBlock = methodBody._if(holder.extras.ne(JExpr._null()))._then();
		}

		JBlock ifContainsKey = holder.extrasNotNullBlock._if(JExpr.invoke(holder.extras, "containsKey").arg(extraKey))._then();

		JTryBlock containsKeyTry = ifContainsKey._try();
		
		JFieldRef extraField = JExpr.ref(fieldName);

		containsKeyTry.body().assign(extraField, JExpr.cast(fieldType, holder.extras.invoke("get").arg(extraKey)));
		
		JCatchBlock containsKeyCatch = containsKeyTry._catch(holder.refClass(ClassCastException.class));
		JVar exceptionParam = containsKeyCatch.param("e");

		JInvocation errorInvoke = holder.refClass("android.util.Log").staticInvoke("e");

		errorInvoke.arg(holder.activity.name());
		errorInvoke.arg("Could not cast extra to expected type, the field is left to its default value");
		errorInvoke.arg(exceptionParam);

		containsKeyCatch.body().add(errorInvoke);
	}

}
