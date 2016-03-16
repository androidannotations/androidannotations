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
open public class HelloAndroidActivity : Activity() {

    @StringRes
    lateinit var hello: String

    @ViewById
    lateinit var helloTextView: TextView

    @AfterViews
    fun afterViews(): Unit {
        computeDateBackground()
    }

    @Background
    open fun computeDateBackground(): Unit {
        val now = Date()
        val helloMessage = String.format(hello, now.toString())

        updateHelloTextView(helloMessage)
    }

    @UiThread
    open fun updateHelloTextView(helloMessage: String): Unit {
        helloTextView.text = helloMessage
    }
}
