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
package org.androidannotations.processing;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.annotations.ECustom;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.processing.EBeansHolder.Classes;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class ECustomProcessor implements GeneratingElementProcessor {
	
	private final String applicationClassName;
	
	public ECustomProcessor(AndroidManifest androidManifest) {
		this.androidManifest = androidManifest.getApplicationClassName();
	}
	
	@Override
	public String getTarget() {
		return ECustom.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		TypeElement typeElement = (TypeElement) element;

		String eCustomQualifiedName = typeElement.getQualifiedName().toString();

		String generatedBeanQualifiedName = eCustomQualifiedName + GENERATION_SUFFIX;

		JDefinedClass generatedClass = codeModel._class(getModifiers(superConstructor.getModifiers()), generatedBeanQualifiedName, ClassType.CLASS);

		EBeanHolder holder = eBeansHolder.create(element, ECustom.class, generatedClass);

		JClass eCustomClass = codeModel.directClass(eCustomQualifiedName);

		holder.generatedClass._extends(eCustomClass);

		Classes classes = holder.classes();

		JFieldVar contextField = holder.generatedClass.field(PRIVATE, classes.CONTEXT, "context_");

		holder.contextRef = contextField;

		JMethod init;
		{
			// init
			init = holder.generatedClass.method(PRIVATE, codeModel.VOID, "init_");
			holder.initBody = init.body();
		}

		{
			// Constructors
			List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());
			
			JClass applicationClass = holder.refClass(applicationQualifiedName + ModelConstants.GENERATION_SUFFIX);

			for (ExecutableElement superConstructor : constructors) {

				JMethod constructor = holder.generatedClass.constructor(getModifiers(superConstructor.getModifiers());

				JBlock constructorBody = constructor.body();
				
				List<? extends VariableElement> superConstructorParameters = superConstructor.getParameters();
				
				JInvocation superInvocation  = constructorBody.invoke("super")
				
				
				for (VariableElement variableElement : superConstructorParameters) {
					TypeElement elementType = variableElement.asType().toString();
					String elementName = variableElement.getSimpleName().toString();
					JVar superContextParam = constructor.param(holder.refClass(elementType), elementName);
					superInvocation.arg(superContextParam);
				}

				constructorBody.assign(contextField, applicationClass.staticInvoke(EApplicationProcessor.GET_APPLICATION_INSTANCE));

				constructorBody.invoke(init);
			}
		}


		{
			// rebind(Context)
			JMethod rebindMethod = holder.generatedClass.method(PUBLIC, codeModel.VOID, "rebind");
			JVar contextParam = rebindMethod.param(classes.CONTEXT, "context");
			JBlock body = rebindMethod.body();
			body.assign(contextField, contextParam);
			body.invoke(init);
		}

	}
	
	 protected int getModifiers(Set<Modifier> modifiers) {
        int mods = 0;
        
        for (Modifier modifier : modifiers) {
            if (modifier.equals(Modifier.ABSTRACT)) {
                mods |= JMod.ABSTRACT;
            } 
            
            if (modifier.equals(Modifier.FINAL)) {
                mods |= JMod.FINAL;
            }
            
            if (modifier.equals(Modifier.NATIVE)) {
                mods |= JMod.NATIVE;
            }
            
            if (modifier.equals(Modifier.PRIVATE)) {
                mods |= JMod.PRIVATE;
            }
            
            if (modifier.equals(Modifier.PROTECTED)) {
                mods |= JMod.PROTECTED;   
            }
            
            if (modifier.equals(Modifier.PUBLIC)) {
                mods |= JMod.PUBLIC;   
            }
            
            if (modifier.equals(Modifier.STATIC)) {
                mods |= JMod.STATIC;   
            }
            
            if (modifier.equals(Modifier.STRICTFP)) {
                mods |= JMod.STRICTFP;
            }
            
            if (modifier.equals(Modifier.SYNCHRONIZED)) {
                mods |= JMod.SYNCHRONIZED;    
            }
            
            if (modifier.equals(Modifier.TRANSIENT)) {
                mods |= JMod.TRANSIENT;   
            }
            
            if (modifier.equals(Modifier.VOLATILE)) {
                mods |= JMod.VOLATILE;    
            }
        }
        
        return mods;
    } 
}
