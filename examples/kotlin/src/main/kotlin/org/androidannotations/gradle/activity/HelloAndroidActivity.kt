package org.androidannotations.gradle.activity

import android.app.Activity
import android.widget.TextView
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.Extra
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

    @Extra
    @JvmField
    protected final var myIntExtra: Int = 0

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

/**
 * As of Kotlin 1.0.6+ and AndroidAnnotations 4.4.0, you can use the kotlin-allopen plugin
 * to remove the need to explicitly declare enhanced classes or methods as <code>open</code>.
 *
 * See the build.gradle file or [our wiki](https://github.com/androidannotations/androidannotations/wiki/Kotlin)
 * for details on how to use the plugin.
 */
@EBean
public class EnhancedBean {

    @Background
    protected fun computeDateBackground() {
        // do stuff ;)
    }
}