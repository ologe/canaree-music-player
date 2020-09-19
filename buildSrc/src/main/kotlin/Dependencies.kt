@file:Suppress("ClassName")

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

object versions {
    val java = JavaVersion.VERSION_1_8

    const val kotlin = "1.4.0"
    const val buildTools = "4.2.0-alpha11"
    const val gms = "4.3.3"
    const val crashlytics = "2.2.1"

    //    core
    internal const val coroutines = "1.3.8"
    internal const val dagger = "2.29.1"
    internal const val hilt = "$dagger-alpha"
    internal const val hiltX = "1.0.0-alpha02"

    //    android x
    internal const val android_x_core = "1.5.0-alpha02"
    internal const val android_x_appcompat = "1.2.0"
    internal const val android_x_media = "1.1.0" // TODO try media2?
    internal const val android_x_recycler = "1.1.0"
    internal const val android_x_browser = "1.2.0"
    internal const val android_x_material = "1.2.0"
    internal const val android_x_preference = "1.1.1"
    internal const val android_x_palette = "1.0.0"
    internal const val android_x_annotations = "1.1.0"
    internal const val android_x_coordinator = "1.1.0"
    internal const val android_x_fragments = "1.3.0-alpha08"
    internal const val constraint_layout = "2.0.0-rc1"
    internal const val lifecycle = "2.2.0"
    internal const val android_x_webview = "1.2.0"

    // compose
    const val compose = "1.0.0-alpha03"

    //    ui
    internal const val glide = "4.11.0"
    internal const val lottie = "3.4.0"
    internal const val custom_tabs = "3.0.1"
    internal const val material_dialogs = "3.3.0"
    internal const val scroll_helper = "1.2.0"
    internal const val blur_kit = "1.0.0"
    internal const val color_desaturation = "1.0.2"
    internal const val jaudiotagger = "2.2.5"
    internal const val tap_target_view = "2.0.0"

    //    data
    internal const val room = "2.2.5"
    internal const val sql_content_resolver = "1.2.3"

    //    network
    internal const val ok_http = "4.8.1"
    internal const val retrofit = "2.9.0"
    internal const val gson = "2.8.6"

    //    utils
    internal const val last_fm_binding = "0.1.2" // TODO remove
    internal const val aes_crypto = "1.1.0"
    internal const val fuzzywuzzy = "1.2.0"

    //    debug
    internal const val leak_canary = "2.4"
    internal const val timber = "4.7.1"
    internal const val chucker = "3.2.0"

    //    firebase
    internal const val firebase_core = "17.2.3"
    internal const val firebase_analytics = "17.5.0"
    internal const val firebase_crashlytics = "17.2.1"
//    firebase_perf = "19.0.5"

    // test
    internal const val junit = "4.12"
    internal const val mockito = "3.4.6"
    internal const val mockitoKotlin = "2.2.0"
    internal const val robolectric = "4.4"
    internal const val android_x_test_core = "1.2.0"

    // 23.0.0 + {buildToolsVersion}
    internal val lint: String
        get() {
            val toolsVersion = buildTools
            val major = toolsVersion.take(1).toInt()
            return "${23 + major}${toolsVersion.drop(1)}"
        }
}

object buildPlugins {

    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val javaLibrary = "java-library"
    const val kotlin = "kotlin"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val crashlytics = "com.google.firebase.crashlytics"
    const val gms = "com.google.gms.google-services"
    const val hilt = "dagger.hilt.android.plugin"

    object classpath {
        const val android = "com.android.tools.build:gradle:${versions.buildTools}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"
        const val gms = "com.google.gms:google-services:${versions.gms}"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:${versions.crashlytics}"
        const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${versions.hilt}"
    }

}

object sdk {

    const val min = 21
    const val target = 30
    const val compile = target

    const val versionCode = 999_29_4_0_00
    const val versionName = "4.0.0"

}

object libs {

    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.kotlin}"

    object dagger {
        const val core = "com.google.dagger:dagger:${versions.dagger}"
        const val kapt = "com.google.dagger:dagger-compiler:${versions.dagger}"
        const val hilt = "com.google.dagger:hilt-android:${versions.hilt}"
        const val hiltKapt = "com.google.dagger:hilt-android-compiler:${versions.hilt}"
        const val hiltX = "androidx.hilt:hilt-lifecycle-viewmodel:${versions.hiltX}"
        const val hiltXKapt = "androidx.hilt:hilt-compiler:${versions.hiltX}"
    }

    object compose {
        const val ui = "androidx.compose.ui:ui:${versions.compose}"
        const val material = "androidx.compose.material:material:${versions.compose}"
        const val materialIcons = "androidx.compose.material:material-icons-extended:${versions.compose}"
        const val tooling = "androidx.ui:ui-tooling:${versions.compose}"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.coroutines}"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${versions.coroutines}"
    }

    object X {
        const val core = "androidx.core:core-ktx:${versions.android_x_core}"
        const val appcompat = "androidx.appcompat:appcompat:${versions.android_x_appcompat}"
        const val media = "androidx.media:media:${versions.android_x_media}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${versions.android_x_recycler}"
        const val browser = "androidx.browser:browser:${versions.android_x_browser}"
        const val material = "com.google.android.material:material:${versions.android_x_material}"
        const val preference = "androidx.preference:preference:${versions.android_x_preference}"
        const val palette = "androidx.palette:palette:${versions.android_x_palette}"
        const val annotations = "androidx.annotation:annotation:${versions.android_x_annotations}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${versions.constraint_layout}"
        const val coordinatorLayout = "androidx.coordinatorlayout:coordinatorlayout:${versions.android_x_coordinator}"
        const val fragments = "androidx.fragment:fragment-ktx:${versions.android_x_fragments}"
        const val webview = "androidx.webkit:webkit:${versions.android_x_webview}"

        object Lifecycle {
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:${versions.lifecycle}"
            const val java8 = "androidx.lifecycle:lifecycle-common-java8:${versions.lifecycle}"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.lifecycle}"
            const val service = "androidx.lifecycle:lifecycle-service:${versions.lifecycle}"
            const val process = "androidx.lifecycle:lifecycle-process:${versions.lifecycle}"
        }

        object Room {
            const val core = "androidx.room:room-runtime:${versions.room}"
            const val coroutines = "androidx.room:room-ktx:${versions.room}"
            const val kapt = "androidx.room:room-compiler:${versions.room}"
            const val test = "androidx.room:room-testing:${versions.room}"
        }

    }

    // testing
    object Test {
        const val junit = "junit:junit:${versions.junit}"
        const val mockito = "org.mockito:mockito-inline:${versions.mockito}"
        const val mockitoKotlin =
            "com.nhaarman.mockitokotlin2:mockito-kotlin:${versions.mockitoKotlin}"
        const val robolectric = "org.robolectric:robolectric:${versions.robolectric}"
        const val android = "androidx.test:core:${versions.android_x_test_core}"
    }

    object Firebase {
        const val core = "com.google.firebase:firebase-core:${versions.firebase_core}"
        const val analytics = "com.google.firebase:firebase-analytics:${versions.firebase_analytics}"
        const val crashlytics = "com.google.firebase:firebase-crashlytics:${versions.firebase_crashlytics}"
        //            perf       = "com.google.firebase:firebase-perf:${versions.firebase_perf}"
    }

    object Network {
        const val okHttp = "com.squareup.okhttp3:okhttp:${versions.ok_http}"
        const val okHttpInterceptor = "com.squareup.okhttp3:logging-interceptor:${versions.ok_http}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${versions.retrofit}"
        const val retrofitGson = "com.squareup.retrofit2:converter-gson:${versions.retrofit}"
        // TODO migrate to moshi??
        const val gson = "com.google.code.gson:gson:${versions.gson}"
    }

    object Utils {
        const val sqlContentResolver =
            "com.github.ologe:android-content-resolver-SQL:${versions.sql_content_resolver}"
        const val lastFmBinding = "de.u-mass:lastfm-java:${versions.last_fm_binding}"
        const val aesCrypto = "com.github.tozny:java-aes-crypto:${versions.aes_crypto}"
        const val fuzzy = "me.xdrop:fuzzywuzzy:${versions.fuzzywuzzy}"
        const val scrollHelper = "com.github.ologe:scroll-helper:${versions.scroll_helper}"
        const val colorDesaturation = "com.github.ologe:color-desaturation:${versions.color_desaturation}"
        const val jaudiotagger = "net.jthink:jaudiotagger:${versions.jaudiotagger}"
    }

    object Debug {
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${versions.leak_canary}"
        const val timber = "com.jakewharton.timber:timber:${versions.timber}"
        const val chucker = "com.github.ChuckerTeam.Chucker:library:${versions.chucker}"
        const val chuckerNoOp = "com.github.ChuckerTeam.Chucker:library-no-op:${versions.chucker}"
    }

    object UX {
        const val glide = "com.github.bumptech.glide:glide:${versions.glide}"
        const val glideKapt = "com.github.bumptech.glide:compiler:${versions.glide}"
        const val blurKit = "io.alterac.blurkit:blurkit:${versions.blur_kit}"
        const val lottie = "com.airbnb.android:lottie:${versions.lottie}"
        const val customTabs = "saschpe.android:customtabs:${versions.custom_tabs}"
        const val dialogs = "com.afollestad.material-dialogs:color:${versions.material_dialogs}"
        const val tapTargetView = "com.github.ologe:taptargetview:${versions.tap_target_view}"
    }

    object Lint {
        val core = "com.android.tools.lint:lint-api:${versions.lint}"
        val checks = "com.android.tools.lint:lint-checks:${versions.lint}"
    }

}

fun DependencyHandlerScope.dagger() {
    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hiltKapt)
    implementation(libs.dagger.hiltX)
    kapt(libs.dagger.hiltXKapt)
}

fun DependencyHandlerScope.coroutines() {
    implementation(libs.Coroutines.core)
    implementation(libs.Coroutines.android)
    testImplementation(libs.Coroutines.test)
}

fun DependencyHandlerScope.compose() {
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.materialIcons)
    implementation(libs.compose.tooling)
}

private fun DependencyHandlerScope.implementation(dependency: Any): Dependency? {
    return add("implementation", dependency)
}

private fun DependencyHandlerScope.testImplementation(dependency: Any): Dependency? {
    return add("testImplementation", dependency)
}

private fun DependencyHandlerScope.kapt(dependency: Any): Dependency? {
    return add("kapt", dependency)
}