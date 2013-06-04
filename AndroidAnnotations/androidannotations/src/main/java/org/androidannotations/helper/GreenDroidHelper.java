package org.androidannotations.helper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GreenDroidHelper {

	public static final String GREENDROID_GD_ACTIVITY_CLASS = "greendroid.app.GDActivity";

	public static final String GREENDROID_GD_LIST_ACTIVITY_CLASS = "greendroid.app.GDListActivity";

	public static final String GREENDROID_GD_TAB_ACTIVITY_CLASS = "greendroid.app.GDTabActivity";

	public static final String GREENDROID_GD_MAP_ACTIVITY_CLASS = "greendroid.app.GDMapActivity";

	public static final List<String> GREENDROID_ACTIVITIES_LIST_CLASS = Arrays.asList(new String[]{ //
			GREENDROID_GD_ACTIVITY_CLASS, //
			GREENDROID_GD_LIST_ACTIVITY_CLASS, //
			GREENDROID_GD_TAB_ACTIVITY_CLASS, //
			GREENDROID_GD_MAP_ACTIVITY_CLASS //
	});

	private ProcessingEnvironment processingEnv;
	private List<TypeElement> greendroidActivityElements;

	public GreenDroidHelper(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		AnnotationHelper annotationHelper = new AnnotationHelper(processingEnv);

		greendroidActivityElements = new ArrayList<TypeElement>();
		for (String greendroidActivityName : GREENDROID_ACTIVITIES_LIST_CLASS) {
			TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(greendroidActivityName);
			if (typeElement != null) {
				greendroidActivityElements.add(typeElement);
			}
		}
	}

	public boolean usesGreenDroid(TypeElement annotatedElement) {
		for (TypeElement greendroidActivityElement : greendroidActivityElements) {
			if (processingEnv.getTypeUtils().isSubtype(annotatedElement.asType(), greendroidActivityElement.asType())) {
				return true;
			}
		}
		return false;
	}
}
