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

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JVar;

public class OptionsMenuProcessor extends AbstractOptionsMenuProcessor {

	public OptionsMenuProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		super(processingEnv, rClass);
	}

	@Override
	public String getTarget() {
		return OptionsMenu.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		super.process(element, codeModel, holder);

		JBlock body = holder.onCreateOptionMenuMethodBody;
		JVar menuInflater = holder.onCreateOptionMenuMenuInflaterVariable;
		JVar menuParam = holder.onCreateOptionMenuMenuParam;

		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(holder, element, Res.MENU, false);

		for (JFieldRef optionsMenuRefId : fieldRefs) {
			body.invoke(menuInflater, "inflate").arg(optionsMenuRefId).arg(menuParam);
		}

	}
}
