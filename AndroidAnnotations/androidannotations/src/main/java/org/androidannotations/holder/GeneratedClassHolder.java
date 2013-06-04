package org.androidannotations.holder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.androidannotations.process.ProcessHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public interface GeneratedClassHolder {
    JDefinedClass getGeneratedClass();
    TypeElement getAnnotatedElement();

    public ProcessingEnvironment processingEnvironment();
    public ProcessHolder.Classes classes();
    public JCodeModel codeModel();
    public JClass refClass(String fullyQualifiedClassName);
    public JClass refClass(Class<?> clazz);
    public JDefinedClass definedClass(String fullyQualifiedClassName);
    public void generateApiClass(Element originatingElement, Class<?> apiClass);
}
