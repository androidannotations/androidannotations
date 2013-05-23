package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.sharedpreferences.*;
import org.androidannotations.api.sharedpreferences.*;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.holder.SharedPrefHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.List;

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.STATIC;

public class SharedPrefHandler extends BaseAnnotationHandler<SharedPrefHolder> implements GeneratingAnnotationHandler<SharedPrefHolder> {

	private IdAnnotationHelper annotationHelper;

	public SharedPrefHandler(ProcessingEnvironment processingEnvironment) {
		super(SharedPref.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public SharedPrefHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new SharedPrefHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		TypeElement typeElement = (TypeElement) element;

		validatorHelper.isInterface(typeElement, valid);

		List<? extends Element> inheritedMembers = processingEnv.getElementUtils().getAllMembers(typeElement);

		for (Element memberElement : inheritedMembers) {
			if (!memberElement.getEnclosingElement().asType().toString().equals("java.lang.Object")) {
				validatorHelper.isPrefMethod(memberElement, valid);
				if (valid.isValid()) {
					validatorHelper.hasCorrectDefaultAnnotation((ExecutableElement) memberElement, valid);
				}
			}
		}

		return valid.isValid();
	}

	@Override
	public void process(Element element, SharedPrefHolder holder) {
		generateApiClasses(element, holder);
		generateConstructor(element, holder);
		generateFieldMethodAndEditorFieldMethod(element, holder);
	}

	private void generateApiClasses(Element originatingElement, SharedPrefHolder holder) {
		holder.generateApiClass(originatingElement, AbstractPrefEditorField.class);
		holder.generateApiClass(originatingElement, AbstractPrefField.class);
		holder.generateApiClass(originatingElement, BooleanPrefEditorField.class);
		holder.generateApiClass(originatingElement, BooleanPrefField.class);
		holder.generateApiClass(originatingElement, EditorHelper.class);
		holder.generateApiClass(originatingElement, FloatPrefEditorField.class);
		holder.generateApiClass(originatingElement, FloatPrefField.class);
		holder.generateApiClass(originatingElement, IntPrefEditorField.class);
		holder.generateApiClass(originatingElement, IntPrefField.class);
		holder.generateApiClass(originatingElement, LongPrefEditorField.class);
		holder.generateApiClass(originatingElement, LongPrefField.class);
		holder.generateApiClass(originatingElement, SharedPreferencesCompat.class);
		holder.generateApiClass(originatingElement, SharedPreferencesHelper.class);
		holder.generateApiClass(originatingElement, StringPrefEditorField.class);
		holder.generateApiClass(originatingElement, StringPrefField.class);
	}

	private void generateConstructor(Element element, SharedPrefHolder holder) {
		SharedPref sharedPrefAnnotation = element.getAnnotation(SharedPref.class);
		SharedPref.Scope scope = sharedPrefAnnotation.value();
		int mode = sharedPrefAnnotation.mode();

		String interfaceSimpleName = element.getSimpleName().toString();
		JBlock constructorSuperBlock = holder.getConstructorSuperBlock();
		JVar contextParam = holder.getConstructorContextParam();

		switch (scope) {
			case ACTIVITY_DEFAULT: {
				JMethod getLocalClassName = getLocalClassName(holder);
				constructorSuperBlock.invoke("super") //
						.arg(contextParam.invoke("getSharedPreferences") //
								.arg(invoke(getLocalClassName).arg(contextParam)) //
								.arg(JExpr.lit(mode)));
				break;
			}
			case ACTIVITY: {
				JMethod getLocalClassName = getLocalClassName(holder);
				constructorSuperBlock.invoke("super") //
						.arg(contextParam.invoke("getSharedPreferences") //
								.arg(invoke(getLocalClassName).arg(contextParam) //
										.plus(JExpr.lit("_" + interfaceSimpleName))) //
								.arg(JExpr.lit(mode)));
				break;
			}
			case UNIQUE: {
				constructorSuperBlock.invoke("super") //
						.arg(contextParam.invoke("getSharedPreferences") //
								.arg(JExpr.lit(interfaceSimpleName)) //
								.arg(JExpr.lit(mode)));
				break;
			}
			case APPLICATION_DEFAULT: {
				JClass preferenceManagerClass = holder.refClass("android.preference.PreferenceManager");
				constructorSuperBlock.invoke("super") //
						.arg(preferenceManagerClass.staticInvoke("getDefaultSharedPreferences") //
								.arg(contextParam));
				break;
			}
		}
	}

	private JMethod getLocalClassName(SharedPrefHolder holder) {

		JClass stringClass = holder.classes().STRING;
		JMethod getLocalClassName = holder.getGeneratedClass().method(PRIVATE | STATIC, stringClass, "getLocalClassName");
		JClass contextClass = holder.classes().CONTEXT;

		JVar contextParam = getLocalClassName.param(contextClass, "context");

		JBlock body = getLocalClassName.body();

		JVar packageName = body.decl(stringClass, "packageName", contextParam.invoke("getPackageName"));

		JVar className = body.decl(stringClass, "className", contextParam.invoke("getClass").invoke("getName"));

		JVar packageLen = body.decl(holder.codeModel().INT, "packageLen", packageName.invoke("length"));

		JExpression condition = className.invoke("startsWith").arg(packageName).not() //
				.cor(className.invoke("length").lte(packageLen)) //
				.cor(className.invoke("charAt").arg(packageLen).ne(lit('.')));

		body._if(condition)._then()._return(className);

		body._return(className.invoke("substring").arg(packageLen.plus(lit(1))));

		return getLocalClassName;
	}

	private void generateFieldMethodAndEditorFieldMethod(Element element, SharedPrefHolder sharedPrefHolder) {
		for(ExecutableElement method : getValidMethods(element)) {
			generateFieldMethod(sharedPrefHolder, method);
			sharedPrefHolder.createEditorFieldMethods(method);
		}
	}

	private List<ExecutableElement> getValidMethods(Element element) {
		List<? extends Element> members = element.getEnclosedElements();
		List<ExecutableElement> methods = ElementFilter.methodsIn(members);
		List<ExecutableElement> validMethods = new ArrayList<ExecutableElement>();
		for (ExecutableElement method : methods) {
			validMethods.add(method);
		}
		return validMethods;
	}

	private void generateFieldMethod(SharedPrefHolder holder, ExecutableElement method) {
		String returnType = method.getReturnType().toString();
		JExpression defaultValue = null;
		if ("boolean".equals(returnType)) {
			DefaultBoolean defaultAnnotation = method.getAnnotation(DefaultBoolean.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.BOOL, JExpr.lit(false), BooleanPrefField.class, "booleanField");
		} else if ("float".equals(returnType)) {
			DefaultFloat defaultAnnotation = method.getAnnotation(DefaultFloat.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.INTEGER, JExpr.lit(0f), FloatPrefField.class, "floatField");
		} else if ("int".equals(returnType)) {
			DefaultInt defaultAnnotation = method.getAnnotation(DefaultInt.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.INTEGER, JExpr.lit(0), IntPrefField.class, "intField");
		} else if ("long".equals(returnType)) {
			DefaultLong defaultAnnotation = method.getAnnotation(DefaultLong.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.INTEGER, JExpr.lit(0l), LongPrefField.class, "longField");
		} else if (CanonicalNameConstants.STRING.equals(returnType)) {
			DefaultString defaultAnnotation = method.getAnnotation(DefaultString.class);
			if (defaultAnnotation != null) {
				defaultValue = JExpr.lit(defaultAnnotation.value());
			}
			createFieldMethod(holder, method, defaultValue, IRClass.Res.STRING, JExpr.lit(""), StringPrefField.class, "stringField");
		}
	}

	private void createFieldMethod(SharedPrefHolder holder, ExecutableElement method, JExpression defaultAnnotationValue, IRClass.Res res, JExpression defValue, Class<?> booleanPrefFieldClass, String fieldHelperMethodName) {
		JExpression defaultValue = defaultAnnotationValue;
		if (defaultAnnotationValue == null) {
			if (method.getAnnotation(DefaultRes.class) != null) {
				defaultValue = extractResValue(holder, method,res);
			} else {
				defaultValue = defValue;
			}
		}
		String fieldName = method.getSimpleName().toString();
		holder.createFieldMethod(booleanPrefFieldClass, fieldName, fieldHelperMethodName, defaultValue);
	}

	private JExpression extractResValue(SharedPrefHolder holder, Element method, IRClass.Res res) {
		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(holder, method, DefaultRes.class.getCanonicalName(), res, true);

		String resourceGetMethodName = null;
		switch (res) {
			case BOOL:
				resourceGetMethodName = "getBoolean";
				break;
			case INTEGER:
				resourceGetMethodName = "getInteger";
				break;
			case STRING:
				resourceGetMethodName = "getString";
				break;
			default:
				break;
		}
		return holder.getContextField().invoke("getResources").invoke(resourceGetMethodName).arg(idRef);
	}
}
