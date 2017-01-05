package org.androidannotations.gradle.activity

import android.app.Activity
import android.widget.TextView
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.UiThread
import org.androidannotations.annotations.Background
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.res.StringRes
import org.androidannotations.gradle.R
import java.util.Date

@EActivity(R.layout.main)
open class HelloAndroidActivity : Activity() {

    @StringRes
    protected lateinit var hello: String

    @ViewById
    protected lateinit var helloTextView: TextView

    @AfterViews
    protected fun afterViews() {
        computeDateBackground()
    }

    @Background
    protected open fun computeDateBackground() {
        val now = Date()
        val helloMessage = String.format(hello, now.toString())

        updateHelloTextView(helloMessage)
    }

    @UiThread
    protected open fun updateHelloTextView(helloMessage: String) {
        helloTextView.text = helloMessage
    }
}
