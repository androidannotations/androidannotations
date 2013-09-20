package org.androidannotations.helper;

import junit.framework.Assert;

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
		Assert.assertEquals("PREFIX_CAMEL_CASE", CaseHelper.camelCaseToUpperSnakeCase("prefix", "camelCase", null));
		Assert.assertEquals("PREFIX_CAMEL_CASE", CaseHelper.camelCaseToUpperSnakeCase("prefix", "prefixCamelCase", null));
		Assert.assertEquals("CAMEL_CASE_SUFFIX", CaseHelper.camelCaseToUpperSnakeCase(null, "camelCase", "suffix"));
		Assert.assertEquals("CAMEL_CASE_SUFFIX", CaseHelper.camelCaseToUpperSnakeCase(null, "camelCaseSuffix", "suffix"));
		Assert.assertEquals("PREFIX_CAMEL_CASE_SUFFIX", CaseHelper.camelCaseToUpperSnakeCase("prefix", "camelCase", "suffix"));
	}

}
