package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to enable the use of HierarchyViewer inside the
 * application.
 * 
 * @author Thomas Fondrillon
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface HierarchyViewerSupport {
}
