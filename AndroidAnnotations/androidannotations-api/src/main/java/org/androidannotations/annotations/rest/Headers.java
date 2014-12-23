/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.annotations.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use on methods in {@link Rest} annotated class to add multiple headers to a given method
 *
 * Example usage
 * @Headers({@Header(headerName="cache-control" value="64000"),
 *           @Header(headerName="keep-alive" value="300")})
 * @Post("/test")
 * public void getTest()
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface Headers {
    Header[] value();
}
