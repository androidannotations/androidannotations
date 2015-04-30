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
package org.androidannotations.test15.rest;

import java.util.Set;

import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Head;
import org.androidannotations.annotations.rest.Options;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

// if defined, the rootUrl will be added as a prefix to every request
@Rest(rootUrl = "http://company.com/ajax/services", converters = { MappingJacksonHttpMessageConverter.class }, interceptors = { RequestInterceptor.class })
public interface HttpMethodsService extends RestClientErrorHandling {

	@Delete("/delete/")
	void delete();

	@Get("/get/")
	void get();

	@Head("/head/")
	HttpHeaders head();

	@Options("/options/")
	Set<HttpMethod> options();

	@Post("/post/")
	void post();

	@Put("/put/")
	void put();

	void setRestTemplate(RestTemplate restTemplate);

}
