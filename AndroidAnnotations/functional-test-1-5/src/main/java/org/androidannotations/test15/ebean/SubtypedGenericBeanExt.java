package org.androidannotations.test15.ebean;

import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class SubtypedGenericBeanExt<S extends List<Integer>> extends SubtypedGenericBean<S, Integer> {
}
