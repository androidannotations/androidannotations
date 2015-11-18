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

import java.util.Set;

import org.androidannotations.rest.spring.annotations.Delete;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Head;
import org.androidannotations.rest.spring.annotations.Options;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(converters = MappingJacksonHttpMessageConverter.class)
public interface ClientWithPathVariable {

	@Delete("/test/{v1}/{v2}")
	void deleteWithParameterEntity(@Path int v1, @Path String v2);

	@Get("/test/{v1}/{v2}")
	void getWithParameterEntity(@Path int v1, @Path String v2);

	@Head("/test/{v1}/{v2}")
	HttpHeaders headWithParameterEntity(@Path int v1, @Path String v2);

	@Options("/test/{v1}/{v2}")
	Set<HttpMethod> optionsWithParameterEntity(@Path int v1, @Path String v2);

	@Post("/test/{v1}/{v2}")
	void postWithParameterEntity(@Path int v1, @Path String v2);

	@Put("/test/{v1}/{v2}")
	void putWithParameterEntity(@Path int v1, @Path String v2);

	@Get("/test/{v1}")
	void getWithPathAnnotation(@Path("v1") int version);

	@Get("/test/{v1}/{v2}")
	void getWithCrossParamAnnotations(@Path("v1") int v2, @Path("v2") int v1);
}
