package org.androidannotations.holder;

import com.sun.codemodel.*;

import static com.sun.codemodel.JMod.PUBLIC;

public class NonConfigurationHolder {

	private JDefinedClass generatedClass;
	private JFieldVar superNonConfigurationInstanceField;

	public NonConfigurationHolder(EActivityHolder eActivityHolder) throws JClassAlreadyExistsException {
		setGeneratedClass(eActivityHolder);
	}

	private void setGeneratedClass(EActivityHolder eActivityHolder) throws JClassAlreadyExistsException {
		generatedClass = eActivityHolder.generatedClass._class(JMod.PRIVATE | JMod.STATIC, "NonConfigurationInstancesHolder");
	}

	public JDefinedClass getGeneratedClass() {
		return generatedClass;
	}

	public JFieldVar getSuperNonConfigurationInstanceField() {
		if (superNonConfigurationInstanceField == null) {
			setSuperNonConfigurationInstanceField();
		}
		return superNonConfigurationInstanceField;
	}

	private void setSuperNonConfigurationInstanceField() {
		superNonConfigurationInstanceField = generatedClass.field(PUBLIC, Object.class, "superNonConfigurationInstance");
	}

	public JFieldVar createField(String fieldName, JClass fieldType) {
		return generatedClass.field(PUBLIC, fieldType, fieldName);
	}
}
