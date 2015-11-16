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
package org.androidannotations.rest.spring.test;

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Part;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientSupport;
import org.springframework.http.converter.FormHttpMessageConverter;

@Rest(converters = { FormHttpMessageConverter.class })
public interface PostRestService extends RestClientSupport {

	@Post("/")
	void post(@Field("otherParam") String postParam, @Field("postParam") String otherParam, @Field String thirdParam);

	@Post("/")
	void multipart(@Part("otherParam") String postParam, @Part("postParam") String otherParam, @Part String thirdParam);

	@Post("/")
	void postRequests(@Body String postParam);
}
