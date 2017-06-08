/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.ElementValidation;

public class ValidatorParameterHelper {

	public interface Validator {
		void validate(ExecutableElement executableElement, ElementValidation validation);
	}

	public interface ParameterRequirement {
		void setMultiple();

		boolean multiple();

		void setOptional();

		boolean required();

		boolean isSatisfied(VariableElement parameter);
	}

	public class NoParamValidator implements Validator {

		@Override
		public void validate(ExecutableElement executableElement, ElementValidation validation) {
			if (!executableElement.getParameters().isEmpty()) {
				validation.addError("%s cannot have any parameters");
			}
		}
	}

	public class OneParamValidator implements Validator {

		private ParameterRequirement parameterRequirement;

		public OneParamValidator(ParameterRequirement param) {
			parameterRequirement = param;
		}

		public OneParamValidator optional() {
			parameterRequirement.setOptional();
			return this;
		}

		public OneParamValidator multiple() {
			parameterRequirement.setMultiple();
			return this;
		}

		@Override
		public void validate(ExecutableElement executableElement, ElementValidation validation) {
			List<? extends VariableElement> parameters = executableElement.getParameters();
			if (!parameterRequirement.multiple()) {
				if (parameterRequirement.required() && parameters.size() != 1) {
					invalidate(validation);
					return;
				}
				if (!parameterRequirement.required() && parameters.size() > 1) {
					invalidate(validation);
					return;
				}
			}

			for (VariableElement parameter : parameters) {
				if (!parameterRequirement.isSatisfied(parameter)) {
					invalidate(validation);
					return;
				}
			}
		}

		protected void invalidate(ElementValidation validation) {
			validation.addError("method annotated with %s can only have the following parameter: " + parameterRequirement);
		}
	}

	private abstract class BaseParamValidator<V extends BaseParamValidator<?>> implements Validator {

		private List<ParameterRequirement> parameterRequirements = new ArrayList<>();
		private List<ParameterRequirement> originalParameterRequirements;

		@Override
		public void validate(ExecutableElement executableElement, ElementValidation validation) {
			originalParameterRequirements = new ArrayList<>(parameterRequirements);
		}

		public V type(String qualifiedName) {
			return param(new ExactTypeParameterRequirement(qualifiedName));
		}

		public V extendsType(String qualifiedName) {
			return param(new ExtendsTypeParameterRequirement(qualifiedName));
		}

		public V extendsAnyOfTypes(String... types) {
			return param(new ExtendsAnyOfTypesParameterRequirement(types));
		}

		public V anyType() {
			return param(new AnyTypeParameterRequirement());
		}

		public V annotatedWith(Class<? extends Annotation> annotationClass) {
			return param(new AnnotatedWithParameterRequirement(annotationClass));
		}

		public V primitiveOrWrapper(TypeKind primitive) {
			return param(new PrimitiveOrWrapperParameterRequirement(primitive));
		}

		public V anyOfPrimitiveOrWrapper(TypeKind... primitives) {
			return param(new AnyOfPrimitiveOrWrapperParameterRequirement(primitives));
		}

		public V anyOfTypes(String... types) {
			return param(new AnyOfTypesParameterRequirement(types));
		}

		public V param(ParameterRequirement parameterRequirement) {
			parameterRequirements.add(parameterRequirement);
			return castThis();
		}

		public V optional() {
			lastParam().setOptional();
			return castThis();
		}

		public V multiple() {
			lastParam().setMultiple();
			return castThis();
		}

		protected List<ParameterRequirement> getParamRequirements() {
			return parameterRequirements;
		}

		private ParameterRequirement lastParam() {
			if (parameterRequirements.isEmpty()) {
				throw new IllegalStateException("Call type, extendsType, annotatedWith or param before");
			}
			return parameterRequirements.get(parameterRequirements.size() - 1);
		}

		protected void invalidate(ExecutableElement element, ElementValidation validation) {
			validation.addError("%s can only have the following parameters: " + createMessage(element));
		}

		protected String createMessage(ExecutableElement element) {
			StringBuilder builder = new StringBuilder();
			builder.append("[ ");
			for (ParameterRequirement parameterRequirement : originalParameterRequirements) {
				builder.append(parameterRequirement).append(", ");
			}
			return builder.append(" ]").toString();
		}

		@SuppressWarnings("unchecked")
		private V castThis() {
			return (V) this;
		}
	}

	public class InOrderParamValidator extends BaseParamValidator<InOrderParamValidator> {

		private int index = -1;
		private ParameterRequirement currentParameterRequirement;
		private List<ParameterRequirement> satisfiedParameterRequirements = new ArrayList<>();

		private void nextParameterRequirement() {
			index++;
			if (index < getParamRequirements().size()) {
				currentParameterRequirement = getParamRequirements().get(index);
			} else {
				currentParameterRequirement = null;
			}
		}

		@Override
		public void validate(ExecutableElement executableElement, ElementValidation validation) {
			super.validate(executableElement, validation);

			nextParameterRequirement();
			for (VariableElement parameter : executableElement.getParameters()) {
				if (!validate(parameter)) {
					invalidate(executableElement, validation);
					return;
				}
			}

			for (ParameterRequirement expectedParameter : getParamRequirements()) {
				if (expectedParameter.required() && !satisfiedParameterRequirements.contains(expectedParameter)) {
					invalidate(executableElement, validation);
					return;
				}
			}
		}

		private boolean validate(VariableElement parameter) {
			if (currentParameterRequirement == null) {
				return false;
			}
			if (currentParameterRequirement.isSatisfied(parameter)) {
				satisfiedParameterRequirements.add(currentParameterRequirement);
				if (!currentParameterRequirement.multiple()) {
					nextParameterRequirement();
				}
			} else {
				if (currentParameterRequirement.required() && !satisfiedParameterRequirements.contains(currentParameterRequirement)) {
					return false;
				} else {
					nextParameterRequirement();
					return validate(parameter);
				}
			}
			return true;
		}

		@Override
		protected String createMessage(ExecutableElement element) {
			return super.createMessage(element) + " in the order above";
		}
	}

	public class AnyOrderParamValidator extends BaseParamValidator<AnyOrderParamValidator> {

		private List<ParameterRequirement> satisfiedParameterRequirements = new ArrayList<>();

		@Override
		public void validate(ExecutableElement executableElement, ElementValidation validation) {
			super.validate(executableElement, validation);

			for (VariableElement parameter : executableElement.getParameters()) {
				ParameterRequirement foundParameter = null;

				for (ParameterRequirement expectedParameter : getParamRequirements()) {
					if (expectedParameter.isSatisfied(parameter)) {
						satisfiedParameterRequirements.add(expectedParameter);
						foundParameter = expectedParameter;
						break;
					}
				}

				if (foundParameter == null) {
					invalidate(executableElement, validation);
					return;
				}

				if (!foundParameter.multiple()) {
					getParamRequirements().remove(foundParameter);
				}
			}

			for (ParameterRequirement expectedParameter : getParamRequirements()) {
				if (expectedParameter.required() && !satisfiedParameterRequirements.contains(expectedParameter)) {
					invalidate(executableElement, validation);
					return;
				}
			}
		}

		@Override
		protected String createMessage(ExecutableElement element) {
			return super.createMessage(element) + " in any order";
		}
	}

	public abstract class BaseParameterRequirement implements ParameterRequirement {

		private boolean required = true;
		private boolean multiple = false;

		@Override
		public void setMultiple() {
			multiple = true;
		}

		@Override
		public boolean multiple() {
			return multiple;
		}

		@Override
		public void setOptional() {
			required = false;
		}

		@Override
		public boolean required() {
			return required;
		}

		protected abstract String description();

		@Override
		public String toString() {
			return String.format("[ %s %s%s]", description(), required ? "" : "(optional) ", multiple ? "(multiple) " : "");
		}
	}

	public class ExactTypeParameterRequirement extends BaseParameterRequirement {

		private String typeName;

		public ExactTypeParameterRequirement(String typeName) {
			this.typeName = typeName;
		}

		@Override
		public boolean isSatisfied(VariableElement param) {
			return param.asType().toString().equals(typeName);
		}

		@Override
		protected String description() {
			return typeName;
		}
	}

	public class ExtendsTypeParameterRequirement extends BaseParameterRequirement {

		private String typeName;

		public ExtendsTypeParameterRequirement(String typeName) {
			this.typeName = typeName;
		}

		@Override
		public boolean isSatisfied(VariableElement param) {
			TypeMirror elementType = param.asType();
			TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(typeName);
			if (typeElement != null) {
				TypeMirror expectedType = typeElement.asType();
				return annotationHelper.isSubtype(elementType, expectedType);
			}
			return false;
		}

		@Override
		protected String description() {
			return "extending " + typeName;
		}
	}

	public class ExtendsAnyOfTypesParameterRequirement extends BaseParameterRequirement {

		private List<String> types;

		public ExtendsAnyOfTypesParameterRequirement(String... types) {
			this.types = Arrays.asList(types);
		}

		@Override
		public boolean isSatisfied(VariableElement parameter) {
			TypeMirror elementType = parameter.asType();

			for (String type : types) {
				TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(type);
				if (typeElement != null) {
					TypeMirror expectedType = typeElement.asType();
					if (annotationHelper.isSubtype(elementType, expectedType)) {
						return true;
					}
				}
			}

			return false;
		}

		@Override
		protected String description() {
			StringBuilder builder = new StringBuilder();
			builder.append("extending one of the following: [");
			for (int i = 0; i < types.size() - 1; ++i) {
				builder.append(types.get(i)).append(", ");
			}
			builder.append(types.get(types.size() - 1)).append(" ]");

			return builder.toString();
		}
	}

	public class AnnotatedWithParameterRequirement extends BaseParameterRequirement {

		private Class<? extends Annotation> annotationClass;

		public AnnotatedWithParameterRequirement(Class<? extends Annotation> annotationClass) {
			this.annotationClass = annotationClass;
		}

		@Override
		public boolean isSatisfied(VariableElement param) {
			return param.getAnnotation(annotationClass) != null;
		}

		@Override
		protected String description() {
			return "annotated with " + annotationClass.getSimpleName();
		}
	}

	public class PrimitiveOrWrapperParameterRequirement extends BaseParameterRequirement {

		private TypeKind type;

		public PrimitiveOrWrapperParameterRequirement(TypeKind type) {
			this.type = type;
		}

		@Override
		protected String description() {
			return type.toString().toLowerCase() + " or " + getWrapperType();
		}

		@Override
		public boolean isSatisfied(VariableElement parameter) {
			return parameter.asType().getKind() == type || parameter.asType().toString().equals(getWrapperType());
		}

		private String getWrapperType() {
			switch (type) {
			case BOOLEAN:
				return CanonicalNameConstants.BOOLEAN;
			case INT:
				return CanonicalNameConstants.INTEGER;
			case BYTE:
				return CanonicalNameConstants.BYTE;
			case SHORT:
				return CanonicalNameConstants.SHORT;
			case LONG:
				return CanonicalNameConstants.LONG;
			case CHAR:
				return CanonicalNameConstants.CHAR;
			case FLOAT:
				return CanonicalNameConstants.FLOAT;
			case DOUBLE:
				return CanonicalNameConstants.DOUBLE;
			default:
				throw new IllegalArgumentException("The TypeKind passed does not represent a primitive");
			}
		}
	}

	public class AnyOfPrimitiveOrWrapperParameterRequirement extends BaseParameterRequirement {

		private List<TypeKind> types;

		public AnyOfPrimitiveOrWrapperParameterRequirement(TypeKind... types) {
			this.types = Arrays.asList(types);
		}

		@Override
		protected String description() {
			StringBuilder builder = new StringBuilder();
			builder.append("Any of the primitives: [");
			for (int i = 0; i < types.size() - 1; ++i) {
				builder.append(getWrapperType(types.get(i))).append(", ");
			}
			builder.append(getWrapperType(types.get(types.size() - 1))).append(" ]");

			return builder.toString();
		}

		@Override
		public boolean isSatisfied(VariableElement parameter) {
			TypeMirror elementType = parameter.asType();

			for (TypeKind type : types) {
				if (elementType.getKind() == type || elementType.toString().equals(getWrapperType(type))) {
					return true;
				}
			}
			return false;
		}

		private String getWrapperType(TypeKind type) {
			switch (type) {
			case BOOLEAN:
				return CanonicalNameConstants.BOOLEAN;
			case INT:
				return CanonicalNameConstants.INTEGER;
			case BYTE:
				return CanonicalNameConstants.BYTE;
			case SHORT:
				return CanonicalNameConstants.SHORT;
			case LONG:
				return CanonicalNameConstants.LONG;
			case CHAR:
				return CanonicalNameConstants.CHAR;
			case FLOAT:
				return CanonicalNameConstants.FLOAT;
			case DOUBLE:
				return CanonicalNameConstants.DOUBLE;
			default:
				throw new IllegalArgumentException("The TypeKind passed does not represent a primitive");
			}
		}
	}

	public class AnyOfTypesParameterRequirement extends BaseParameterRequirement {

		private List<String> types;

		public AnyOfTypesParameterRequirement(String... types) {
			this.types = Arrays.asList(types);
		}

		@Override
		public boolean isSatisfied(VariableElement parameter) {
			return types.contains(parameter.asType().toString());
		}

		@Override
		protected String description() {
			return Arrays.toString(types.toArray());
		}
	}

	public class AnyTypeParameterRequirement extends BaseParameterRequirement {
		@Override
		public boolean isSatisfied(VariableElement parameter) {
			return true;
		}

		@Override
		protected String description() {
			return "any type";
		}
	}

	public Validator noParam() {
		return new NoParamValidator();
	}

	public OneParamValidator type(String qualifiedName) {
		return param(new ExactTypeParameterRequirement(qualifiedName));
	}

	public OneParamValidator extendsType(String qualifiedName) {
		return param(new ExtendsTypeParameterRequirement(qualifiedName));
	}

	public OneParamValidator extendsAnyOfTypes(String... types) {
		return param(new ExtendsAnyOfTypesParameterRequirement(types));
	}

	public OneParamValidator anyType() {
		return param(new AnyTypeParameterRequirement());
	}

	public OneParamValidator annotatedWith(Class<? extends Annotation> annotationClass) {
		return param(new AnnotatedWithParameterRequirement(annotationClass));
	}

	public OneParamValidator primitiveOrWrapper(TypeKind primitive) {
		return param(new PrimitiveOrWrapperParameterRequirement(primitive));
	}

	public OneParamValidator anyOfTypes(String... types) {
		return param(new AnyOfTypesParameterRequirement(types));
	}

	public OneParamValidator param(ParameterRequirement parameterRequirement) {
		return new OneParamValidator(parameterRequirement);
	}

	public InOrderParamValidator inOrder() {
		return new InOrderParamValidator();
	}

	public AnyOrderParamValidator anyOrder() {
		return new AnyOrderParamValidator();
	}

	protected final TargetAnnotationHelper annotationHelper;

	public ValidatorParameterHelper(TargetAnnotationHelper targetAnnotationHelper) {
		annotationHelper = targetAnnotationHelper;
	}
}
