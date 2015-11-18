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

import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.FormHttpMessageConverter;

@Rest(converters = FormHttpMessageConverter.class)
public interface ClientWithPostParameters {

	@Post("/")
	void emptyPost();

	@Post("/")
	void oneField(@Field String a);

	@Post("/")
	void oneFieldWithName(@Field("b") String a);

	@Post("/")
	void twoField(@Field String a, @Field String b);

	@Post("/")
	void twoFieldsWithName(@Field String a, @Field("c") String b);

	@Post("/")
	void twoFieldsWithCrossName(@Field("b") String a, @Field("a") String b);

	@Post("/")
	void twoFieldssOneWithName(@Field String a, @Field("c") String b);

	@Post("/{url}")
	void fieldAndUrlVariable(@Field String a, @Path String url);

	@Post("/")
	void fieldClassPathResource(@Field ClassPathResource res);
}

