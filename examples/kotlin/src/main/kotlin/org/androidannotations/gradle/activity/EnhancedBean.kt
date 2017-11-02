package org.androidannotations.gradle.activity

import org.androidannotations.annotations.Background
import org.androidannotations.annotations.EBean

/**
 * As of Kotlin 1.0.6+ and AndroidAnnotations 4.4.0, you can use the kotlin-allopen plugin
 * to remove the need to explicitly declare enhanced classes or methods as <code>open</code>.
 *
 * See the build.gradle file or [our wiki](https://github.com/androidannotations/androidannotations/wiki/Kotlin-support)
 * for details on how to use the plugin.
 */
@EBean
class EnhancedBean {

    @Background
    protected fun computeDateBackground() {
        // do stuff ;)
    }
}
