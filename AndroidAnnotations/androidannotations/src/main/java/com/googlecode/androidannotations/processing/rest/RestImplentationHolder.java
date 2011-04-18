package com.googlecode.androidannotations.processing.rest;

import java.util.HashMap;
import java.util.Map;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

public class RestImplentationHolder {

    public JDefinedClass restImplementationClass;

    private Map<String, JClass> loadedClasses = new HashMap<String, JClass>();

	public JFieldVar restTemplateField;

	public String urlPrefix;

    public JClass refClass(String fullyQualifiedClassName) {

        JClass refClass = loadedClasses.get(fullyQualifiedClassName);

        if (refClass == null) {
            refClass = restImplementationClass.owner().ref(fullyQualifiedClassName);
            loadedClasses.put(fullyQualifiedClassName, refClass);
        }

        return refClass;
    }

}
