package org.androidannotations.test15.ebean;

import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class SubtypedGenericBeanExt2 extends SubtypedGenericBean<List<Double>, Double> {
}
