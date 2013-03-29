/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.processing.rest;

import java.util.TreeMap;

import javax.lang.model.element.Element;

import org.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JVar;

public class MethodProcessorHolder {

	private Element element;
	private String urlSuffix;
	private JClass expectedClass;
	private JClass methodReturnClass;
	private JCodeModel codeModel;

	private JBlock body;
	private TreeMap<String, JVar> methodParams;
	private final EBeanHolder holder;

	public MethodProcessorHolder(EBeanHolder holder, Element element, String urlSuffix, JClass expectedClass, JClass generatedReturnType, JCodeModel codeModel) {
		this.holder = holder;
		this.element = element;
		this.urlSuffix = urlSuffix;
		this.expectedClass = expectedClass;
		this.methodReturnClass = generatedReturnType;
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

	public void setExpectedClass(JClass expectedClass) {
		this.expectedClass = expectedClass;
	}

	public JClass getMethodReturnClass() {
		return methodReturnClass;
	}

	public void setMethodReturnClass(JClass methodReturnClass) {
		this.methodReturnClass = methodReturnClass;
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

	public EBeanHolder getHolder() {
		return holder;
	}

}
