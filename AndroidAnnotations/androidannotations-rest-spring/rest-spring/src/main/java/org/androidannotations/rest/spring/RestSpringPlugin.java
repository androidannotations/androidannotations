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
package org.androidannotations.rest.spring;

import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;
import org.androidannotations.rest.spring.handler.DeleteHandler;
import org.androidannotations.rest.spring.handler.GetHandler;
import org.androidannotations.rest.spring.handler.HeadHandler;
import org.androidannotations.rest.spring.handler.OptionsHandler;
import org.androidannotations.rest.spring.handler.PostHandler;
import org.androidannotations.rest.spring.handler.PutHandler;
import org.androidannotations.rest.spring.handler.RestHandler;
import org.androidannotations.rest.spring.handler.RestServiceHandler;

import javax.annotation.processing.ProcessingEnvironment;

public class RestSpringPlugin extends AndroidAnnotationsPlugin {

	private static final String NAME = "REST-Spring";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void addHandlers(AnnotationHandlers annotationHandlers, ProcessingEnvironment processingEnvironment) {
		annotationHandlers.add(new RestHandler(processingEnvironment));
		annotationHandlers.add(new GetHandler(processingEnvironment));
		annotationHandlers.add(new PostHandler(processingEnvironment));
		annotationHandlers.add(new PutHandler(processingEnvironment));
		annotationHandlers.add(new DeleteHandler(processingEnvironment));
		annotationHandlers.add(new HeadHandler(processingEnvironment));
		annotationHandlers.add(new OptionsHandler(processingEnvironment));
		annotationHandlers.add(new RestServiceHandler(processingEnvironment));
	}
}
