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
package org.androidannotations.helper;

import org.junit.Assert;
import org.junit.Test;

public class CaseHelperTest {

	@Test
	public void testCamelCaseToUpperSnakeCaseString() throws Exception {
		Assert.assertEquals("CAMEL_CASE", CaseHelper.camelCaseToUpperSnakeCase("camelCase"));
		Assert.assertEquals("CAMEL_CASE", CaseHelper.camelCaseToUpperSnakeCase("camel_case"));
		Assert.assertEquals("CAMEL_CASE", CaseHelper.camelCaseToUpperSnakeCase("Camel_Case"));
	}

	@Test
	public void testCamelCaseToUpperSnakeCaseStringStringString() throws Exception {
		Assert.assertEquals("CAMEL_CASE", CaseHelper.camelCaseToUpperSnakeCase(null, "camelCase", null));
		Assert.assertEquals("CAMEL2_CASE", CaseHelper.camelCaseToUpperSnakeCase(null, "camel2Case", null));
		Assert.assertEquals("PREFIX_CAMEL_CASE", CaseHelper.camelCaseToUpperSnakeCase("prefix", "camelCase", null));
		Assert.assertEquals("PREFIX_CAMEL_CASE", CaseHelper.camelCaseToUpperSnakeCase("prefix", "prefixCamelCase", null));
		Assert.assertEquals("CAMEL_CASE_SUFFIX", CaseHelper.camelCaseToUpperSnakeCase(null, "camelCase", "suffix"));
		Assert.assertEquals("CAMEL_CASE_SUFFIX", CaseHelper.camelCaseToUpperSnakeCase(null, "camelCaseSuffix", "suffix"));
		Assert.assertEquals("PREFIX_CAMEL_CASE_SUFFIX", CaseHelper.camelCaseToUpperSnakeCase("prefix", "camelCase", "suffix"));
	}

}
