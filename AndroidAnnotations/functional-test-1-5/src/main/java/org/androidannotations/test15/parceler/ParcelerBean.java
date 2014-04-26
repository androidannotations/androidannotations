package org.androidannotations.test15.parceler;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * @author John Ericksen
 */
@Parcel
public class ParcelerBean {

    String name;
    int age;

    public ParcelerBean() {
    }

    @ParcelConstructor
    public ParcelerBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
