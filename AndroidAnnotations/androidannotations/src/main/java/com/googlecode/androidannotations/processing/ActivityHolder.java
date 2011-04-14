package com.googlecode.androidannotations.processing;

import java.util.HashMap;
import java.util.Map;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class ActivityHolder {

    public JDefinedClass activity;
    public JMethod beforeSetContentView;
    public JVar beforeSetContentViewSavedInstanceStateParam;
    public JMethod afterSetContentView;
    public JBlock extrasNotNullBlock;
    public JVar extras;
    public JClass bundleClass;
    public JVar resources;

    private Map<String, JClass> loadedClasses = new HashMap<String, JClass>();
    public JFieldVar handler;

    public JClass refClass(String fullyQualifiedClassName) {

        JClass refClass = loadedClasses.get(fullyQualifiedClassName);

        if (refClass == null) {
            refClass = activity.owner().ref(fullyQualifiedClassName);
            loadedClasses.put(fullyQualifiedClassName, refClass);
        }

        return refClass;
    }

    public JClass refClass(Class<?> clazz) {
        return activity.owner().ref(clazz);
    }

}
