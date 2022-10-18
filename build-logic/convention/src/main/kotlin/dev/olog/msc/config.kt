@file:Suppress("ClassName")

package dev.olog.msc

import org.gradle.api.JavaVersion

object config {

    const val minSdk = 21
    const val targetSdk = 32
    const val compileSdk = 32

    /*
        version code
        999 - for old compatibility
        27 - android version
        X.xxx - X major version, xxx minor version
    */
    const val versionCode = 999_29_3_3_01
    const val versionName = "3.3.1"

    val javaVersion = JavaVersion.VERSION_11

}