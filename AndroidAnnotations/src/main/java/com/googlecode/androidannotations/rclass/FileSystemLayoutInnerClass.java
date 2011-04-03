package com.googlecode.androidannotations.rclass;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

public class FileSystemLayoutInnerClass implements IRInnerClass{
    
    private final Map<Integer, String> idQualifiedNamesByIdValues = new HashMap<Integer, String>();

    private final String rInnerQualifiedName;
    
    public FileSystemLayoutInnerClass(File resFolder,  String rClassPackage) throws IOException{
        
        rInnerQualifiedName = rClassPackage+".layout";
        
        File layoutFolder = new File(resFolder, "layout");

        File[] layoutFiles = layoutFolder.listFiles();
        
        for(File layoutFile : layoutFiles) {
            idQualifiedNamesByIdValues.put(0, "");
        }
        
    }
    

    @Override
    public boolean containsIdValue(Integer idValue) {
        return idQualifiedNamesByIdValues.containsKey(idValue);
    }

    @Override
    public String getIdQualifiedName(Integer idValue) {
        return idQualifiedNamesByIdValues.get(idValue);
    }

    @Override
    public boolean containsField(String name) {
        return idQualifiedNamesByIdValues.containsValue(rInnerQualifiedName + "." + name);
    }

    @Override
    public String getIdQualifiedName(String name) {
        String idQualifiedName = rInnerQualifiedName + "." + name;

        if (idQualifiedNamesByIdValues.containsValue(idQualifiedName)) {
            return idQualifiedName;
        } else {
            return null;
        }

    }

    @Override
    public JFieldRef getIdStaticRef(Integer idValue, JCodeModel codeModel) {
        String layoutFieldQualifiedName = getIdQualifiedName(idValue);
        return extractIdStaticRef(codeModel, layoutFieldQualifiedName);
    }

    @Override
    public JFieldRef getIdStaticRef(String name, JCodeModel codeModel) {
        String layoutFieldQualifiedName = getIdQualifiedName(name);
        return extractIdStaticRef(codeModel, layoutFieldQualifiedName);
    }

    private JFieldRef extractIdStaticRef(JCodeModel codeModel, String layoutFieldQualifiedName) {
        if (layoutFieldQualifiedName != null) {
            int fieldSuffix = layoutFieldQualifiedName.lastIndexOf('.');
            String fieldName = layoutFieldQualifiedName.substring(fieldSuffix + 1);
            String rInnerClassName = layoutFieldQualifiedName.substring(0, fieldSuffix);

            return codeModel.ref(rInnerClassName).staticRef(fieldName);
        } else {
            return null;
        }
    }

}
