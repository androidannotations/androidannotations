package com.googlecode.androidannotations.rclass;

import java.io.IOException;

import com.googlecode.androidannotations.model.AnnotationElements;

public interface RClassFinder {

    IRClass find(AnnotationElements extractedModel) throws IOException ;

}