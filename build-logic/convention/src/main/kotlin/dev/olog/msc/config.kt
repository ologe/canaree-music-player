package dev.olog.msc

import org.gradle.api.JavaVersion

@Suppress("ClassName")
object config {
    // Sdk and tools
    const val minSdkVersion = 21
    const val targetSdkVersion = 31
    const val compileSdkVersion = 31

    /*  version code
        999 - for old compatibility
        27 - android version
        X.xxx - X major version, xxx minor version
     */
    const val versionCode = 999_29_3_3_01
    const val versionName = "3.3.1"

    val javaVersion = JavaVersion.VERSION_11
}