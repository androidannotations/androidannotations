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
package org.androidannotations.handler;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class IgnoredWhenDetachedHandler extends BaseAnnotationHandler<EComponentHolder> {

	private final TargetAnnotationHelper annotationHelper;

	public IgnoredWhenDetachedHandler(ProcessingEnvironment processingEnvironment) {
		super(IgnoredWhenDetached.class, processingEnvironment);
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validateHasOnlyUIThreadOrBackground(element, valid);

		validatorHelper.enclosingElementHasEFragment(element, validatedElements, valid);
	}

	private void validateHasOnlyUIThreadOrBackground(Element element, IsValid valid) {
		UiThread uiThread = element.getAnnotation(UiThread.class);
		Background background = element.getAnnotation(Background.class);
		if ((uiThread == null && background == null) || (uiThread != null && background != null)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "can only be used on a method that uses either " + TargetAnnotationHelper.annotationName(Background.class) + " or " + TargetAnnotationHelper.annotationName(UiThread.class));
		}
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {

	}
}
