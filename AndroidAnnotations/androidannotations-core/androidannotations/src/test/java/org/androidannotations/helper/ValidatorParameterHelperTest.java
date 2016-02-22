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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.ElementValidation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.TextView;

public class ValidatorParameterHelperTest {

	private ValidatorParameterHelper validator;

	@Before
	public void setup() {
		TargetAnnotationHelper targetAnnotationHelper = mock(TargetAnnotationHelper.class);

		doAnswer(new IsSubtypeAnswer()).when(targetAnnotationHelper).isSubtype(any(TypeMirror.class), any(TypeMirror.class));
		doAnswer(new TypeElementFromQualifiedNameAnswer()).when(targetAnnotationHelper).typeElementFromQualifiedName(any(String.class));

		validator = new ValidatorParameterHelper(targetAnnotationHelper);
	}

	@Test
	public void anyOfTypes() throws Exception {
		ExecutableElement executableElement = createMethod(Integer.class, Long.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.anyOfTypes(CanonicalNameConstants.INTEGER, CanonicalNameConstants.LONG).multiple().validate(executableElement, valid);
		assertTrue(valid.isValid());
	}

	@Test
	public void extendsViewType() throws Exception {
		ExecutableElement executableElement = createMethod(TextView.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.extendsType(CanonicalNameConstants.VIEW).validate(executableElement, valid);
		assertTrue(valid.isValid());
	}

	@Test
	public void primitiveParam() throws Exception {
		ExecutableElement executableElement = createMethod(int.class, Integer.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.primitiveOrWrapper(TypeKind.INT).multiple().validate(executableElement, valid);
		assertTrue(valid.isValid());
	}

	@Test
	public void annotatedParam() throws Exception {
		ExecutableElement executableElement = createMethod(p(String.class, TestAnnotation.class));

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.annotatedWith(TestAnnotation.class).validate(executableElement, valid);
		assertTrue(valid.isValid());
	}

	@Test
	public void optionalStringParam() throws Exception {
		ExecutableElement executableElement = createMethod(String.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.type(CanonicalNameConstants.STRING).optional().validate(executableElement, valid);
		assertTrue(valid.isValid());

		ExecutableElement withoutArgument = createMethod();

		ElementValidation valid2 = new ElementValidation("", executableElement);
		validator.type(CanonicalNameConstants.STRING).optional().validate(withoutArgument, valid2);
		assertTrue(valid2.isValid());
	}

	@Test
	public void stringParam() throws Exception {
		ExecutableElement executableElement = createMethod(String.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.type(CanonicalNameConstants.STRING).validate(executableElement, valid);
		assertTrue(valid.isValid());

		executableElement = createMethod(int.class);
		valid = new ElementValidation("", executableElement);
		validator.noParam().validate(executableElement, valid);
		assertFalse(valid.isValid());
	}

	@Test
	public void anyParam() throws Exception {
		ExecutableElement executableElement = createMethod(String.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.anyType().validate(executableElement, valid);
		assertTrue(valid.isValid());
	}

	@Test
	public void noParam() throws Exception {
		ExecutableElement executableElement = createMethod();

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.noParam().validate(executableElement, valid);
		assertTrue(valid.isValid());

		executableElement = createMethod(int.class);
		valid = new ElementValidation("", executableElement);
		validator.noParam().validate(executableElement, valid);
		assertFalse(valid.isValid());
	}

	@Test
	public void inOrderSuccess() throws Exception {
		ExecutableElement executableElement = createMethod(boolean.class, int.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.inOrder() //
				.type(boolean.class.getName()) //
				.type(long.class.getName()).optional() //
				.type(int.class.getName()) //
				.validate(executableElement, valid);
		assertTrue(valid.isValid());
	}

	@Test
	public void inOrderSuccess2() throws Exception {
		ExecutableElement executableElement = createMethod(boolean.class, boolean.class, int.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.inOrder() //
				.type(boolean.class.getName()).multiple() //
				.type(long.class.getName()).optional() //
				.type(int.class.getName()) //
				.validate(executableElement, valid);
		assertTrue(valid.isValid());
	}

	@Test
	public void inOrderFail() throws Exception {
		ExecutableElement executableElement = createMethod(AdapterView.class, long.class, boolean.class, Bundle.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.inOrder() //
				.type(boolean.class.getName()).optional() //
				.type(long.class.getName()).optional() //
				.type(int.class.getName()).optional() //
				.validate(executableElement, valid);
		assertFalse(valid.isValid());
	}

	@Test
	public void inOrderFail2() throws Exception {
		ExecutableElement executableElement = createMethod(long.class, boolean.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.inOrder() //
				.type(boolean.class.getName()).multiple() //
				.type(long.class.getName()).optional() //
				.type(int.class.getName()).optional() //
				.validate(executableElement, valid);
		assertFalse(valid.isValid());
	}

	@Test
	public void inOrderFail3() throws Exception {
		ExecutableElement executableElement = createMethod(boolean.class, boolean.class, long.class, Bundle.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.inOrder() //
				.type(boolean.class.getName()).multiple() //
				.type(long.class.getName()).optional() //
				.type(int.class.getName()).optional() //
				.validate(executableElement, valid);
		assertFalse(valid.isValid());
	}

	@Test
	public void inOrderFail4() throws Exception {
		ExecutableElement executableElement = createMethod(int.class, long.class, boolean.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.inOrder() //
				.type(boolean.class.getName()).optional() //
				.type(long.class.getName()).optional() //
				.type(int.class.getName()).optional() //
				.validate(executableElement, valid);
		assertFalse(valid.isValid());
	}

	@Test
	public void itemSelect() {
		ExecutableElement executableElement = createMethod(boolean.class, int.class);

		ElementValidation valid = new ElementValidation("", executableElement);
		validator.inOrder() //
				.primitiveOrWrapper(TypeKind.BOOLEAN) //
				.anyType().optional() //
				.validate(executableElement, valid);
		assertTrue(valid.isValid());
	}

	private static ExecutableElement createMethod(Class<?> firstParamClass, Class<?>... paramClasses) {
		Param[] params = new Param[paramClasses.length + 1];
		params[0] = new Param(firstParamClass, null);
		for (int i = 0; i < paramClasses.length; i++) {
			params[i + 1] = new Param(paramClasses[i], null);
		}
		return createMethod(params);
	}

	private static ExecutableElement createMethod(Param... params) {
		List<VariableElement> paramList = new ArrayList<>();
		for (Param param : params) {
			VariableElement variableElement = mock(VariableElement.class);
			doReturn(param.type.getName()).when(variableElement).toString();
			if (param.annotationType != null) {
				doReturn(mock(param.annotationType)).when(variableElement).getAnnotation(param.annotationType);
			}

			doReturn(mockTypeMirror(param.type)).when(variableElement).asType();

			paramList.add(variableElement);
		}

		ExecutableElement executableElement = mock(ExecutableElement.class);
		doReturn(paramList).when(executableElement).getParameters();
		return executableElement;
	}

	private static Param p(Class<?> type, Class<? extends Annotation> annotationType) {
		return new Param(type, annotationType);
	}

	private static TypeMirror mockTypeMirror(Class<?> type) {
		ClassAwareTypeMirror typeMirror = mock(ClassAwareTypeMirror.class);
		doReturn(type.getCanonicalName()).when(typeMirror).toString();
		doReturn(getKindOfType(type)).when(typeMirror).getKind();
		doReturn(type).when(typeMirror).getMirrorClass();
		return typeMirror;
	}

	private static TypeKind getKindOfType(Class<?> type) {
		if (!type.isPrimitive()) {
			return TypeKind.DECLARED;
		}
		return TypeKind.valueOf(type.getName().toUpperCase());
	}

	private @interface TestAnnotation {
	}

	private interface ClassAwareTypeMirror extends TypeMirror {
		Class<?> getMirrorClass();
	}

	private static final class IsSubtypeAnswer implements Answer<Object> {
		@Override
		public Boolean answer(InvocationOnMock invocation) throws Throwable {
			Object[] args = invocation.getArguments();
			ensureValidArgs(args);

			Class<?> subClass = ((ClassAwareTypeMirror) args[0]).getClass();
			Class<?> superClass = ((ClassAwareTypeMirror) args[1]).getClass();

			return superClass.isAssignableFrom(subClass);
		}

		private void ensureValidArgs(Object[] args) {
			if (args.length != 2) {
				throw new IllegalArgumentException("invalid argument count");
			}
			if (!(args[0] instanceof ClassAwareTypeMirror)) {
				throw new IllegalArgumentException("first argument has to be an instance of " + ClassAwareTypeMirror.class);
			}
			if (!(args[1] instanceof ClassAwareTypeMirror)) {
				throw new IllegalArgumentException("second argument has to be an instance of " + ClassAwareTypeMirror.class);
			}
		}
	}

	private static final class TypeElementFromQualifiedNameAnswer implements Answer<TypeElement> {
		@Override
		public TypeElement answer(InvocationOnMock invocation) throws Throwable {
			Object[] args = invocation.getArguments();
			ensureValidArgs(args);

			TypeElement typeElement = mock(TypeElement.class);
			Class<?> typeClass = Class.forName((String) args[0]);
			doReturn(mockTypeMirror(typeClass)).when(typeElement).asType();

			return typeElement;
		}

		private void ensureValidArgs(Object[] args) {
			if (args.length != 1) {
				throw new IllegalArgumentException("invalid argument count");
			}
			if (!(args[0] instanceof String)) {
				throw new IllegalArgumentException("first argument has to be an instance of " + String.class);
			}
		}
	}

	private static final class Param {
		final Class<?> type;
		final Class<? extends Annotation> annotationType;

		Param(Class<?> type, Class<? extends Annotation> annotationType) {
			this.type = type;
			this.annotationType = annotationType;
		}
	}
}
