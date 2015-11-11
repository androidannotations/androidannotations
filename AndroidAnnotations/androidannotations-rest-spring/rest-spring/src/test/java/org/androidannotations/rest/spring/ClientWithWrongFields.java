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

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Part;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.converter.FormHttpMessageConverter;

@Rest(converters = FormHttpMessageConverter.class)
public interface ClientWithWrongFields {

	@Post("/duplicateField")
	void duplicateField(@Field("v1") int v1, @Field("v1") int v2);

	@Post("/conflictWithPathParam")
	void conflictWithPathParam(@Field("pathParamConflict") int v1, @Path("pathParamConflict") int v2);

	@Post("/conflictWithPathParamWithElementName")
	void conflictWithPathParamWithElementName(@Field("elementNameConflict") int v1, @Path("elementNameConflict") int elementNameConflict);

	@Post("/conflictElementNameWithPathParam")
	void conflictElementNameWithPathParam(@Field int conflict, @Path("conflict") int v2);

	@Post("/pathParamAndEntity")
	void fieldAndEntity(@Field int v1, @Body String entity);

	void missingPostAnnotation(@Field("missingPost") int v1);

	@Post("/fieldAndPartOnSameMethod")
	void fieldAndPartOnSameMethod(@Field String field, @Part String part);

	@Post("/fieldAndBodyOnSameMethod")
	void fieldAndBodyOnSameMethod(@Field String field, @Body String part);
}
