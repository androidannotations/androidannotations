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

import java.util.Set;

import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Head;
import org.androidannotations.annotations.rest.Options;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(converters = MappingJacksonHttpMessageConverter.class)
public interface ClientWithPathVariable {

	@Delete("/test/{v1}/{v2}")
	void deleteWithParameterEntity(int v1, String v2);

	@Get("/test/{v1}/{v2}")
	void getWithParameterEntity(int v1, String v2);

	@Head("/test/{v1}/{v2}")
	HttpHeaders headWithParameterEntity(int v1, String v2);

	@Options("/test/{v1}/{v2}")
	Set<HttpMethod> optionsWithParameterEntity(int v1, String v2);

	@Post("/test/{v1}/{v2}")
	void postWithParameterEntity(int v1, String v2);

	@Put("/test/{v1}/{v2}")
	void putWithParameterEntity(int v1, String v2);

}
