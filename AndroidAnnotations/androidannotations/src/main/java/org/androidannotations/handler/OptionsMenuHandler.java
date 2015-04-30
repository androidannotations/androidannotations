/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.handler;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasOptionsMenu;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JVar;

public class OptionsMenuHandler extends BaseAnnotationHandler<HasOptionsMenu> {

	private IdAnnotationHelper annotationHelper;

	public OptionsMenuHandler(ProcessingEnvironment processingEnvironment) {
		super(OptionsMenu.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.hasEActivityOrEFragment(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.MENU, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, valid);
	}

	@Override
	public void process(Element element, HasOptionsMenu holder) {
		JBlock body = holder.getOnCreateOptionsMenuMethodBody();
		JVar menuInflater = holder.getOnCreateOptionsMenuMenuInflaterVar();
		JVar menuParam = holder.getOnCreateOptionsMenuMenuParam();

		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(processHolder, element, IRClass.Res.MENU, false);
		for (JFieldRef optionsMenuRefId : fieldRefs) {
			body.invoke(menuInflater, "inflate").arg(optionsMenuRefId).arg(menuParam);
		}

	}
}
