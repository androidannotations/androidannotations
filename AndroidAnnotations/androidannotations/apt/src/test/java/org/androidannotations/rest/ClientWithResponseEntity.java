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
package org.androidannotations.rest;

import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Head;
import org.androidannotations.annotations.rest.Options;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(converters = MappingJacksonHttpMessageConverter.class)
public interface ClientWithResponseEntity {

	@Delete("/test/")
	Entity deleteWithReturnType();

	@Get("/test/")
	Entity getWithReturnType();

	@Head("/test/")
	Entity headWithReturnType();

	@Options("/test/")
	Entity optionsWithReturnType();

	@Post("/test/")
	Entity postWithReturnType();

	@Put("/test/")
	Entity putWithReturnType();

}
