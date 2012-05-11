/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing.rest;

import java.util.TreeMap;

import javax.lang.model.element.Element;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JVar;

public class MethodProcessorHolder {

	private Element element;
	private String urlSuffix;
	private JClass expectedClass;
	private JClass generatedReturnType;
	private JCodeModel codeModel;

	private JBlock body;
	private TreeMap<String, JVar> methodParams;

	public MethodProcessorHolder(Element element, String urlSuffix, JClass expectedClass, JClass generatedReturnType, JCodeModel codeModel) {
		this.element = element;
		this.urlSuffix = urlSuffix;
		this.expectedClass = expectedClass;
		this.generatedReturnType = generatedReturnType;
		this.codeModel = codeModel;
	}

	public Element getElement() {
		return element;
	}

	public String getUrlSuffix() {
		return urlSuffix;
	}

	public JClass getExpectedClass() {
		return expectedClass;
	}

	public JClass getGeneratedReturnType() {
		return generatedReturnType;
	}

	public JCodeModel getCodeModel() {
		return codeModel;
	}

	public JBlock getBody() {
		return body;
	}

	public void setBody(JBlock body) {
		this.body = body;
	}

	public TreeMap<String, JVar> getMethodParams() {
		return methodParams;
	}

	public void setMethodParams(TreeMap<String, JVar> methodParams) {
		this.methodParams = methodParams;
	}

}
