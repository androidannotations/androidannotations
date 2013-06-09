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

import static com.sun.codemodel.JExpr.ref;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JVar;

/**
 */
public class OptionsMenuItemProcessor extends AbstractOptionsMenuProcessor {

	public OptionsMenuItemProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv, rClass);
	}

	@Override
	public String getTarget() {
		return OptionsMenuItem.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		super.process(element, codeModel, holder);

		String fieldName = element.getSimpleName().toString();
		JBlock body = holder.onCreateOptionMenuMethodBody;
		JVar menuParam = holder.onCreateOptionMenuMenuParam;

		JFieldRef idsRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, Res.ID, true);

		body.assign(ref(fieldName), menuParam.invoke("findItem").arg(idsRef));

	}
}
