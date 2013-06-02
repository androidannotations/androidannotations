package org.androidannotations.holder;

import com.sun.codemodel.*;

public interface HasInstanceState extends GeneratedClassHolder {
    JBlock getSaveStateMethodBody();
    JVar getSaveStateBundleParam();
    JMethod getRestoreStateMethod();
    JVar getRestoreStateBundleParam();
}