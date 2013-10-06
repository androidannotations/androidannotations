/**
 * Copyright (C) 2010-2012 eBusiness Informatikkon, Excilys Group
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

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.annotations.NonParcelable;
import org.androidannotations.annotations.Parcelable;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.ModelConstants;

import android.os.Parcel;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * Generates the code for elements which are annotated with @Parcelable
 */
public class ParcelableProcessor implements GeneratingElementProcessor {

	private final AnnotationHelper annotationHelper;

	public ParcelableProcessor(ProcessingEnvironment processingEnv) {
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public String getTarget() {
		return Parcelable.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		TypeElement typeElement = (TypeElement) element;
		String beanQualifiedName = typeElement.getQualifiedName().toString();
		String generatedBeanQualifiedName = beanQualifiedName + GENERATION_SUFFIX;

		JClass annotatedClass = codeModel.directClass(beanQualifiedName);
		JClass subClass = codeModel.directClass(generatedBeanQualifiedName);

		// Class declaration
		JDefinedClass generatedClass = codeModel._class(PUBLIC | FINAL, generatedBeanQualifiedName, ClassType.CLASS);
		generatedClass._extends(annotatedClass);
		generatedClass._implements(android.os.Parcelable.class);

		// Get all suitable fields
		List<VariableElement> suitableVariables = getSuitableVariable(typeElement);

		// Default constructor with no parameter
		generatedClass.constructor(JMod.PUBLIC);

		// Constructor with initial bean as parameter
		JMethod beanConstructor = generatedClass.constructor(JMod.PUBLIC);
		beanConstructor.param(annotatedClass, "bean");
		JBlock beanCBody = beanConstructor.body();
		for (VariableElement suitableElement : suitableVariables) {
			String boxedClassname = getBoxedClass(codeModel, suitableElement);
			// The type is a Parcelable: create a new wrapped instance
			if (isParcelable(boxedClassname)) {
				beanCBody.directStatement(suitableElement.getSimpleName() + " = new " + boxedClassname + ModelConstants.GENERATION_SUFFIX + "(bean." + suitableElement.getSimpleName() + ");");
			}
			// Non Parcelable case
			else {
				beanCBody.directStatement(suitableElement.getSimpleName() + " = bean." + suitableElement.getSimpleName() + ";");
			}
		}

		// Constructor with Parcel input as parameter
		JMethod parcelConstructor = generatedClass.constructor(JMod.PUBLIC);
		parcelConstructor.param(codeModel.ref(Parcel.class), "parcel");
		JBlock parcelCBody = parcelConstructor.body();
		JVar classLoader = parcelCBody.decl(codeModel.ref(ClassLoader.class), "classLoader");
		classLoader.init(JExpr._this().invoke("getClass").invoke("getClassLoader"));
		for (VariableElement suitableElement : suitableVariables) {
			String boxedClassname = getBoxedClass(codeModel, suitableElement);
			parcelCBody.directStatement(suitableElement.getSimpleName() + " = (" + boxedClassname + ") parcel.readValue(classLoader);");
		}

		// The describeContents method
		JMethod describeContents = generatedClass.method(JMod.PUBLIC, codeModel.INT, "describeContents");
		describeContents.annotate(Override.class);
		describeContents.body()._return(JExpr.lit(0));

		// The writeToParcel method
		JMethod writeToParcel = generatedClass.method(JMod.PUBLIC, codeModel.VOID, "writeToParcel");
		writeToParcel.annotate(Override.class);
		JVar dest = writeToParcel.param(codeModel.ref(Parcel.class), "dest");
		writeToParcel.param(codeModel.INT, "flags");
		for (VariableElement suitableElement : suitableVariables) {
			writeToParcel.body().add(dest.invoke("writeValue").arg(JExpr.ref(suitableElement.getSimpleName().toString())));
		}

		// Parcelable.Creator static anonymous class
		JClass creatorClass = codeModel.ref(android.os.Parcelable.Creator.class).narrow(subClass);
		JFieldVar creator = generatedClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, creatorClass, "CREATOR");
		JDefinedClass creatorAnonymousClass = codeModel.anonymousClass(creatorClass);
		JMethod createFromParcel = creatorAnonymousClass.method(JMod.PUBLIC, subClass, "createFromParcel");
		JVar parcel = createFromParcel.param(codeModel.ref(Parcel.class), "parcel");
		createFromParcel.body()._return(JExpr._new(subClass).arg(parcel));
		//
		JMethod newArray = creatorAnonymousClass.method(JMod.PUBLIC, subClass.array(), "newArray");
		JVar size = newArray.param(codeModel.INT, "size");
		newArray.body()._return(JExpr.newArray(subClass, size));

		creator.init(JExpr._new(creatorAnonymousClass));
	}

	/**
	 * Return the name of a given element's class (or the name of the boxed
	 * class if the given variable element is a primitive type).
	 */
	private String getBoxedClass(JCodeModel codeModel, VariableElement variable) {
		TypeMirror typeMirror = variable.asType();
		if (typeMirror.getKind().isPrimitive()) {
			TypeElement boxedElement = annotationHelper.getBoxedClass((PrimitiveType) typeMirror);
			return boxedElement.asType().toString();
		}
		return typeMirror.toString();
	}

	/**
	 * Returns true if the TypeElement of the classname has Parcelable
	 * annotations
	 * 
	 * @param className
	 * @return
	 */
	private boolean isParcelable(String className) {
		TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(className);
		if (typeElement != null) {
			return typeElement.getAnnotation(Parcelable.class) != null;
		}
		return false;
	}

	/**
	 * Returns all suitable variable element from the TypeElement parameter. A
	 * suitable variable element is a VariableElement enclosed by the
	 * TypeElement which is not private, nor transient
	 * 
	 * @param typeElement
	 * @return
	 */
	private List<VariableElement> getSuitableVariable(TypeElement typeElement) {
		List<VariableElement> result = new ArrayList<VariableElement>();

		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		List<VariableElement> fieldsElement = ElementFilter.fieldsIn(enclosedElements);
		for (VariableElement fieldElement : fieldsElement) {
			// Generate only if @NonParcelable is not present
			if (fieldElement.getAnnotation(NonParcelable.class) == null) {
				// Verify if the field is not private nor transient
				if (!annotationHelper.isPrivate(fieldElement) && !annotationHelper.isTransient(fieldElement)) {
					result.add(fieldElement);
				}
			}
		}
		return result;
	}

}
