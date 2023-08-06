@file:Suppress("ClassName")

package dev.olog.msc

import org.gradle.api.JavaVersion

object config {

    val minSdk = 21
    val targetSdk = 30
    val compileSdk = 30

    /*  version code
        999 - for old compatibility
        27 - android version
        X.xxx - X major version, xxx minor version
     */
    val versionCode = 999_29_3_3_01
    val versionName = "3.3.1"

    val javaVersion = JavaVersion.VERSION_1_8

}