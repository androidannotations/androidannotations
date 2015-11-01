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

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;
import org.androidannotations.rest.spring.handler.BodyHandler;
import org.androidannotations.rest.spring.handler.DeleteHandler;
import org.androidannotations.rest.spring.handler.FieldHandler;
import org.androidannotations.rest.spring.handler.GetHandler;
import org.androidannotations.rest.spring.handler.HeadHandler;
import org.androidannotations.rest.spring.handler.HeaderHandler;
import org.androidannotations.rest.spring.handler.HeadersHandler;
import org.androidannotations.rest.spring.handler.OptionsHandler;
import org.androidannotations.rest.spring.handler.PartHandler;
import org.androidannotations.rest.spring.handler.PatchHandler;
import org.androidannotations.rest.spring.handler.PathHandler;
import org.androidannotations.rest.spring.handler.PostHandler;
import org.androidannotations.rest.spring.handler.PutHandler;
import org.androidannotations.rest.spring.handler.RestHandler;
import org.androidannotations.rest.spring.handler.RestServiceHandler;

public class RestSpringPlugin extends AndroidAnnotationsPlugin {

	private static final String NAME = "REST-Spring";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<AnnotationHandler<?>> getHandlers(AndroidAnnotationsEnvironment androidAnnotationEnv) {
		List<AnnotationHandler<?>> annotationHandlers = new ArrayList<>();
		annotationHandlers.add(new RestHandler(androidAnnotationEnv));
		annotationHandlers.add(new FieldHandler(androidAnnotationEnv));
		annotationHandlers.add(new PartHandler(androidAnnotationEnv));
		annotationHandlers.add(new BodyHandler(androidAnnotationEnv));
		annotationHandlers.add(new GetHandler(androidAnnotationEnv));
		annotationHandlers.add(new PostHandler(androidAnnotationEnv));
		annotationHandlers.add(new PutHandler(androidAnnotationEnv));
		annotationHandlers.add(new PatchHandler(androidAnnotationEnv));
		annotationHandlers.add(new DeleteHandler(androidAnnotationEnv));
		annotationHandlers.add(new HeadHandler(androidAnnotationEnv));
		annotationHandlers.add(new OptionsHandler(androidAnnotationEnv));
		annotationHandlers.add(new PathHandler(androidAnnotationEnv));
		annotationHandlers.add(new HeaderHandler(androidAnnotationEnv));
		annotationHandlers.add(new HeadersHandler(androidAnnotationEnv));
		annotationHandlers.add(new RestServiceHandler(androidAnnotationEnv));
		return annotationHandlers;
	}
}
