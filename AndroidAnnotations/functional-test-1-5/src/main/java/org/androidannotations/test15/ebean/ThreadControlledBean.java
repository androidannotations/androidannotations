package org.androidannotations.test15.ebean;

import org.androidannotations.annotations.*;

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

    @SupposeUiThread
    @UiThread
    public void uiSupposedAndUi(Runnable delegate) {
        delegate.run();
    }

    @SupposeBackground(serial = SERIAL1)
    @Background(serial = SERIAL2)
    public void backgroundSupposeAndBackground(Runnable delegate) {
        delegate.run();
    }

}
