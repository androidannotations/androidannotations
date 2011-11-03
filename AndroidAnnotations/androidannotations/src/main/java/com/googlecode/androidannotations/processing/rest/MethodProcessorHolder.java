package com.googlecode.androidannotations.processing.rest;

import java.util.TreeMap;

import javax.lang.model.element.Element;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JVar;

public class MethodProcessorHolder {

	private Element element;
	private String url;
	private JClass expectedClass;
	private JClass generatedReturnType;
	private JCodeModel codeModel;
	
	private JBlock body;
	private TreeMap<String, JVar> methodParams;
	
	public MethodProcessorHolder(Element element, String url, JClass expectedClass, JClass generatedReturnType, JCodeModel codeModel) {
		this.element = element;
		this.url = url;
		this.expectedClass = expectedClass;
		this.generatedReturnType = generatedReturnType;
		this.codeModel = codeModel;
	}

	public Element getElement() {
		return element;
	}
	
	public String getUrl() {
		return url;
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
