/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
    public JMethod beforeCreate;
    public JVar beforeCreateSavedInstanceStateParam;
    public JMethod afterSetContentView;
    public JBlock extrasNotNullBlock;
    public JVar extras;
    public JClass bundleClass;
    public JVar resources;
    
    public JMethod cast;

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
