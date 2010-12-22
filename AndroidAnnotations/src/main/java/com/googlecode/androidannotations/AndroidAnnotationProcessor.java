package com.googlecode.androidannotations;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({ "com.googlecode.androidannotations.Layout", "com.googlecode.androidannotations.UiField" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AndroidAnnotationProcessor extends AbstractProcessor {

	private static final String LAYOUT_INNER_CLASS_NAME = "layout";

	private static final String ID_INNER_CLASS_NAME = "id";

	List<TypeElement> layoutAnnotatedElements = new ArrayList<TypeElement>();

	List<VariableElement> uiFieldAnnotatedElements = new ArrayList<VariableElement>();

	Map<Element, GeneratedActivity> activitiesByElement = new HashMap<Element, GeneratedActivity>();

	Map<Integer, String> layoutFieldQualifiedNamesByIds = new HashMap<Integer, String>();

	Map<Integer, String> idFieldQualifiedNamesByIds = new HashMap<Integer, String>();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		try {
			clear();

			parseElements(annotations, roundEnv);

			if (foundLayoutAnnotatedElements()) {

				TypeElement rClassElement = findRClassElement();

				List<TypeElement> rInnerTypes = extractRInnerTypes(rClassElement);

				extractLayoutFieldIds(rInnerTypes);

				extractIdFieldIds(rInnerTypes);

				for (TypeElement layoutAnnotatedElement : layoutAnnotatedElements) {
					Layout layoutAnnotation = layoutAnnotatedElement.getAnnotation(Layout.class);
					int layoutId = layoutAnnotation.value();

					if (!layoutFieldQualifiedNamesByIds.containsKey(layoutId)) {
						AnnotationMirror annotationMirror = findAnnotationMirror(layoutAnnotatedElement, Layout.class);
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Layout id value not found in R.layout.*: " + layoutId,
								layoutAnnotatedElement, annotationMirror);
					} else {

						String layoutFieldQualifiedName = layoutFieldQualifiedNamesByIds.get(layoutId);

						String superClassQualifiedName = layoutAnnotatedElement.getQualifiedName().toString();

						int packageSeparatorIndex = superClassQualifiedName.lastIndexOf('.');

						String packageName = superClassQualifiedName.substring(0, packageSeparatorIndex);

						String classSimpleName = superClassQualifiedName.substring(packageSeparatorIndex + 1)+"_";

						GeneratedActivity activity = new GeneratedActivity();

						activity.setLayoutQualifiedName(layoutFieldQualifiedName);
						activity.setClassSimpleName(classSimpleName);
						activity.setPackageName(packageName);

						activity.setSuperClassQualifiedName(superClassQualifiedName);

						activitiesByElement.put(layoutAnnotatedElement, activity);

					}
				}

				for (VariableElement uiFieldAnnotatedElement : uiFieldAnnotatedElements) {

					Element enclosingElement = uiFieldAnnotatedElement.getEnclosingElement();

					GeneratedActivity generatedActivity = activitiesByElement.get(enclosingElement);

					if (generatedActivity != null) {
						List<GeneratedField> generatedFields = generatedActivity.getGeneratedFields();

						String name = uiFieldAnnotatedElement.getSimpleName().toString();

						TypeMirror uiFieldTypeMirror = uiFieldAnnotatedElement.asType();

						if (uiFieldTypeMirror instanceof DeclaredType) {

							DeclaredType uiFieldDeclaredType = (DeclaredType) uiFieldTypeMirror;
							String typeQualifiedName = uiFieldDeclaredType.toString();

							UiField uiFieldAnnotation = uiFieldAnnotatedElement.getAnnotation(UiField.class);

							int id = uiFieldAnnotation.value();

							String viewQualifiedId = null;
							if (id == -1) {
								String fieldName = uiFieldAnnotatedElement.getSimpleName().toString();

								TypeElement idType = extractIdInnerType(rInnerTypes);

								String idQualifiedName = idType.getQualifiedName().toString();

								viewQualifiedId = idQualifiedName + "." + fieldName;
								
								if (!idFieldQualifiedNamesByIds.containsValue(viewQualifiedId)) {
									AnnotationMirror annotationMirror = findAnnotationMirror(uiFieldAnnotatedElement, UiField.class);
									processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
											UiField.class + " field name not found in R.id.* " + Layout.class+ " "+viewQualifiedId, uiFieldAnnotatedElement, annotationMirror);
									viewQualifiedId = null;
								}

							} else {
								if (!idFieldQualifiedNamesByIds.containsKey(id)) {
									AnnotationMirror annotationMirror = findAnnotationMirror(uiFieldAnnotatedElement, UiField.class);
									processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
											UiField.class + " value not found in R.id.* " + Layout.class, uiFieldAnnotatedElement, annotationMirror);
								} else {
									viewQualifiedId = idFieldQualifiedNamesByIds.get(id);
								}
							}

							if (viewQualifiedId != null) {
								GeneratedField generatedField = new GeneratedField();

								generatedField.setName(name);
								generatedField.setTypeQualifiedName(typeQualifiedName);
								generatedField.setViewQualifiedId(viewQualifiedId);

								generatedFields.add(generatedField);
							}
						} else {
							AnnotationMirror annotationMirror = findAnnotationMirror(uiFieldAnnotatedElement, UiField.class);
							processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
									UiField.class + " should only be used on a field which is a declared type " + Layout.class, uiFieldAnnotatedElement,
									annotationMirror);
						}

					}

				}

				Filer filer = processingEnv.getFiler();

				for (GeneratedActivity activity : activitiesByElement.values()) {
					activity.generateSource(filer);
				}
			}

			clear();
		} catch (CompilationFailedException e) {
			return false;
		} catch (Exception e) {
			Element element = roundEnv.getElementsAnnotatedWith(annotations.iterator().next()).iterator().next();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Annotation processing exception: " + e.toString(), element);
		}

		return false;
	}

	private TypeElement findRClassElement() {

		Elements elementUtils = processingEnv.getElementUtils();

		TypeElement firstActivity = layoutAnnotatedElements.get(0);

		PackageElement firstActivityPackage = elementUtils.getPackageOf(firstActivity);

		TypeElement rType = elementUtils.getTypeElement(firstActivityPackage.getQualifiedName() + "." + "R");

		// TODO better handling at finding R class
		if (rType == null) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
					"All Activities should belong to the same package as the R class, which is not the case for: " + firstActivity, firstActivity);
		}

		return rType;
	}

	private AnnotationMirror findAnnotationMirror(Element annotatedElement, Class<? extends Annotation> annotationClass) {
		List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();

		for (AnnotationMirror annotationMirror : annotationMirrors) {
			TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
			if (isAnnotation(annotationElement, annotationClass)) {
				return annotationMirror;
			}
		}
		throw new CompilationFailedException();
	}

	private void extractLayoutFieldIds(List<TypeElement> rInnerTypes) {

		TypeElement layoutType = extractLayoutInnerType(rInnerTypes);

		if (layoutType == null) {
			return;
		}

		String layoutQualifiedName = layoutType.getQualifiedName().toString();

		List<? extends Element> layoutEnclosedElements = layoutType.getEnclosedElements();

		List<VariableElement> layoutFields = ElementFilter.fieldsIn(layoutEnclosedElements);

		for (VariableElement layoutField : layoutFields) {
			TypeKind fieldType = layoutField.asType().getKind();
			if (fieldType.isPrimitive() && fieldType.equals(TypeKind.INT)) {
				Integer layoutFieldId = (Integer) layoutField.getConstantValue();
				layoutFieldQualifiedNamesByIds.put(layoutFieldId, layoutQualifiedName + "." + layoutField.getSimpleName());
			}
		}
	}

	private void extractIdFieldIds(List<TypeElement> rInnerTypes) {

		TypeElement idType = extractIdInnerType(rInnerTypes);

		if (idType == null) {
			return;
		}

		String idQualifiedName = idType.getQualifiedName().toString();

		List<? extends Element> idEnclosedElements = idType.getEnclosedElements();

		List<VariableElement> idFields = ElementFilter.fieldsIn(idEnclosedElements);

		for (VariableElement idField : idFields) {
			TypeKind fieldType = idField.asType().getKind();
			if (fieldType.isPrimitive() && fieldType.equals(TypeKind.INT)) {
				Integer idFieldId = (Integer) idField.getConstantValue();
				idFieldQualifiedNamesByIds.put(idFieldId, idQualifiedName + "." + idField.getSimpleName());
			}
		}
	}

	private TypeElement extractIdInnerType(List<TypeElement> rInnerTypes) {

		for (TypeElement rInnerType : rInnerTypes) {
			if (rInnerType.getSimpleName().toString().equals(ID_INNER_CLASS_NAME)) {
				return rInnerType;
			}
		}

		return null;
	}

	private TypeElement extractLayoutInnerType(List<TypeElement> rInnerTypes) {

		for (TypeElement rInnerType : rInnerTypes) {
			if (rInnerType.getSimpleName().toString().equals(LAYOUT_INNER_CLASS_NAME)) {
				return rInnerType;
			}
		}

		return null;
	}

	private List<TypeElement> extractRInnerTypes(TypeElement rElement) {

		List<? extends Element> rEnclosedElements = rElement.getEnclosedElements();

		List<TypeElement> rInnerTypes = ElementFilter.typesIn(rEnclosedElements);
		return rInnerTypes;
	}

	private boolean foundLayoutAnnotatedElements() {
		return layoutAnnotatedElements.size() != 0;
	}

	// private TypeElement extractRClassElement(Element rLocationElement) {
	// RClass rLocationAnnotation =
	// rLocationElement.getAnnotation(RClass.class);
	// try {
	// rLocationAnnotation.value();
	// } catch (MirroredTypeException mte) {
	// DeclaredType typeMirror = (DeclaredType) mte.getTypeMirror();
	// return (TypeElement) typeMirror.asElement();
	// }
	// processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
	// "Annotation processing error : could not extract MirrorType from class value",
	// rLocationElement);
	// throw new IllegalArgumentException();
	// }

	private void clear() {
		layoutAnnotatedElements.clear();
		layoutFieldQualifiedNamesByIds.clear();
		activitiesByElement.clear();
		uiFieldAnnotatedElements.clear();
	}

	private void parseElements(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		TypeMirror activityTypeMirror = processingEnv.getElementUtils().getTypeElement("android.app.Activity").asType();

		for (TypeElement annotation : annotations) {
			for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

				if (isAnnotation(annotation, Layout.class)) {
					TypeElement typeElement = (TypeElement) element;

					TypeMirror layoutTypeMirror = typeElement.asType();

					if (processingEnv.getTypeUtils().isSubtype(layoutTypeMirror, activityTypeMirror)) {
						layoutAnnotatedElements.add(typeElement);
					} else {
						AnnotationMirror annotationMirror = findAnnotationMirror(typeElement, Layout.class);
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, Layout.class + " should only be used on Activity subclasses",
								typeElement, annotationMirror);
					}
				} else if (isAnnotation(annotation, UiField.class)) {
					VariableElement variableElement = (VariableElement) element;

					Element enclosingElement = variableElement.getEnclosingElement();

					if (enclosingElement instanceof TypeElement) {
						TypeElement enclosingTypeElement = (TypeElement) enclosingElement;

						Layout layoutAnnotation = enclosingTypeElement.getAnnotation(Layout.class);

						if (layoutAnnotation != null) {
							uiFieldAnnotatedElements.add(variableElement);
						} else {
							AnnotationMirror annotationMirror = findAnnotationMirror(variableElement, UiField.class);
							processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
									UiField.class + " should only be used on a field from a class annotated with " + Layout.class, variableElement,
									annotationMirror);

						}

					} else {
						AnnotationMirror annotationMirror = findAnnotationMirror(variableElement, UiField.class);
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, UiField.class + " should only be used on a field from a class",
								variableElement, annotationMirror);

					}
				}
			}
		}
	}

	private boolean isAnnotation(TypeElement annotation, Class<? extends Annotation> annotationClass) {
		return annotation.getQualifiedName().toString().equals(annotationClass.getName());
	}

}