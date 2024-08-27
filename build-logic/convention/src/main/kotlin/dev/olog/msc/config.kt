@file:Suppress("ClassName", "ConstPropertyName")

package dev.olog.msc

import org.gradle.api.JavaVersion

internal object config {

    const val minSdk = 21
    const val targetSdk = 34
    const val compileSdk = 34

    /**
     * version code
     * 999 - for legacy compatibility
     * 27 - android version
     * X.xxx - X major version, xxx minor version
     */
    const val versionCode = 999_29_3_3_01
    const val versionName = "3.3.1"

    val java = JavaVersion.VERSION_17

}