package com.sun.codemodel;

import java.util.Iterator;
import java.util.List;

public class JSuperWildcard extends JClass {

    private final JClass bound;

    public JSuperWildcard(JClass bound) {
        super(bound.owner());
        this.bound = bound;
    }

    public String name() {
        return "? super "+ bound.name();
    }

    public String fullName() {
        return "? super "+ bound.fullName();
    }

    public JPackage _package() {
        return null;
    }

    /**
     * Returns the class bound of this variable.
     *
     * <p>
     * If no bound is given, this method returns {@link Object}.
     */
    public JClass _extends() {
        if(bound !=null)
            return bound;
        else
            return owner().ref(Object.class);
    }

    /**
     * Returns the interface bounds of this variable.
     */
    public Iterator<JClass> _implements() {
        return bound._implements();
    }

    public boolean isInterface() {
        return false;
    }

    public boolean isAbstract() {
        return false;
    }

    protected JClass substituteParams(JTypeVar[] variables, List<JClass> bindings) {
        JClass nb = bound.substituteParams(variables,bindings);
        if(nb== bound)
            return this;
        else
            return nb.wildcard();
    }

    public void generate(JFormatter f) {
        if(bound._extends()==null)
            f.p("?");   // instead of "? extends Object"
        else
            f.p("? super").g(bound);
    }
}
