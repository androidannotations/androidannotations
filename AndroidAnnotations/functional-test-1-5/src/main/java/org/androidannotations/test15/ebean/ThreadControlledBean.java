package org.androidannotations.test15.ebean;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SupposeBackground;
import org.androidannotations.annotations.SupposeUiThread;

@EBean
public class ThreadControlledBean {

    public static final String SERIAL1 = "serial1";
    public static final String SERIAL2 = "serial2";

    @SupposeUiThread
    public void uiSupposed() {
    }

    @SupposeBackground
    public void backgroundSupposed() {
    }

    @SupposeBackground(serial = {SERIAL1, SERIAL2})
    public void serialBackgroundSupposed() {
    }

}
