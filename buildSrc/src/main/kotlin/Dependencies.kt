import Libraries.Versions.aes_crypto
import Libraries.Versions.android_x_annotations
import Libraries.Versions.android_x_appcompat
import Libraries.Versions.android_x_browser
import Libraries.Versions.android_x_coordinator
import Libraries.Versions.android_x_core
import Libraries.Versions.android_x_fragments
import Libraries.Versions.android_x_legacy
import Libraries.Versions.android_x_material
import Libraries.Versions.android_x_media
import Libraries.Versions.android_x_palette
import Libraries.Versions.android_x_preference
import Libraries.Versions.android_x_recycler
import Libraries.Versions.android_x_test_core
import Libraries.Versions.android_x_webview
import Libraries.Versions.blur_kit
import Libraries.Versions.color_desaturation
import Libraries.Versions.constraint_layout
import Libraries.Versions.coroutines
import Libraries.Versions.custom_tabs
import Libraries.Versions.dagger
import Libraries.Versions.daggerAssisted
import Libraries.Versions.firebase_analytics
import Libraries.Versions.firebase_core
import Libraries.Versions.firebase_crashlytics
import Libraries.Versions.fuzzywuzzy
import Libraries.Versions.last_fm_binding
import Libraries.Versions.leak_canary
import Libraries.Versions.lifecycle
import Libraries.Versions.lint
import Libraries.Versions.material_dialogs
import Libraries.Versions.room
import Libraries.Versions.scroll_helper
import Libraries.Versions.sql_content_resolver
import Libraries.Versions.tap_target_view
import Libraries.Versions.workManager

const val kotlinVersion = "1.3.72"

object BuildPlugins {

    object Versions {
        const val buildToolsVersion = "4.0.0-beta04"
        const val gms = "4.3.3"
        const val fabric = "1.31.2"
    }

    object Classpath {
        const val androidGradlePlugin =
            "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        const val gms = "com.google.gms:google-services:${Versions.gms}"
        const val fabric = "io.fabric.tools:gradle:${Versions.fabric}"
    }

    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val javaLibrary = "java-library"
    const val kotlin = "kotlin"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val fabric = "io.fabric"
    const val gms = "com.google.gms.google-services"

}

object AndroidSdk {

    const val min = 21
    const val target = 29
    const val compile = target

    const val versionCode = 999_29_4_0_00
    const val versionName = "4.0.0"

}

object Libraries {

    object Versions {

        //    core
        internal const val coroutines = "1.3.5"
        internal const val dagger = "2.27"
        internal const val daggerAssisted = "0.5.2"

        //    android x
        internal const val android_x_core = "1.2.0"
        internal const val android_x_legacy = "1.0.0"
        internal const val android_x_appcompat = "1.1.0"
        internal const val android_x_media = "1.1.0"
        internal const val android_x_recycler = "1.1.0"
        internal const val android_x_browser = "1.2.0"
        internal const val android_x_material = "1.2.0-alpha06"
        internal const val android_x_preference = "1.1.0"
        internal const val android_x_palette = "1.0.0"
        internal const val android_x_annotations = "1.1.0"
        internal const val android_x_coordinator = "1.1.0"
        internal const val android_x_fragments = "1.2.4"
        internal const val constraint_layout = "2.0.0-beta4"
        internal const val lifecycle = "2.2.0"
        internal const val android_x_webview = "1.2.0"

        //    ui
        internal const val glide = "4.11.0"
        internal const val lottie = "3.4.0"
        internal const val custom_tabs = "3.0.1"
        internal const val material_dialogs = "3.3.0"
        internal const val scroll_helper = "2.0.0-beta04"
        internal const val blur_kit = "1.0.0"
        internal const val color_desaturation = "1.0.2"
        internal const val jaudiotagger = "2.2.5"
        internal const val tap_target_view = "2.0.0"

        //    data
        internal const val room = "2.2.5"
        internal const val workManager = "2.3.4"
        internal const val sql_content_resolver = "1.2.3"

        //    network
        internal const val ok_http = "4.4.1"
        internal const val retrofit = "2.7.2"
        internal const val gson = "2.8.6"

        //    utils
        internal const val last_fm_binding = "0.1.2" // TODO remove
        internal const val aes_crypto = "1.1.0"
        internal const val fuzzywuzzy = "1.2.0"

        //    debug
        internal const val leak_canary = "2.2"
        internal const val stetho = "1.5.1"
        internal const val timber = "4.7.1"
        internal const val chucker = "3.1.2"

        //    firebase
        internal const val firebase_core = "17.2.3"
        internal const val firebase_analytics = "17.2.3"
        internal const val firebase_crashlytics = "2.10.1"
//    firebase_perf = "19.0.5"

        // test
        internal const val junit = "4.12"
        internal const val mockito = "3.2.4"
        internal const val mockitoKotlin = "2.2.0"
        internal const val robolectric = "4.3.1"
        internal const val android_x_test_core = "1.2.0"

        // 23.0.0 + {buildToolsVersion}
        internal val lint: String
            get() {
                val toolsVersion = BuildPlugins.Versions.buildToolsVersion
                val major = toolsVersion.take(1).toInt()
                return "${23 + major}${toolsVersion.drop(1)}"
            }

    }

    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"

    object Dagger {
        const val core = "com.google.dagger:dagger:$dagger"
        const val kapt = "com.google.dagger:dagger-compiler:$dagger"
        const val android = "com.google.dagger:dagger-android:$dagger"
        const val androidSupport = "com.google.dagger:dagger-android-support:$dagger"
        const val androidKapt = "com.google.dagger:dagger-android-processor:$dagger"
        const val assisted = "com.squareup.inject:assisted-inject-annotations-dagger2:$daggerAssisted"
        const val assistedKapt = "com.squareup.inject:assisted-inject-processor-dagger2:$daggerAssisted"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines"
    }

    object X {
        const val core = "androidx.core:core-ktx:$android_x_core"
        const val legacy =
            "androidx.legacy:legacy-support-v4:$android_x_legacy" // TODO try to remove
        const val appcompat = "androidx.appcompat:appcompat:$android_x_appcompat"
        const val media = "androidx.media:media:$android_x_media"
        const val recyclerView = "androidx.recyclerview:recyclerview:$android_x_recycler"
        const val browser = "androidx.browser:browser:$android_x_browser"
        const val material = "com.google.android.material:material:$android_x_material"
        const val preference = "androidx.preference:preference:$android_x_preference"
        const val palette = "androidx.palette:palette:$android_x_palette"
        const val annotations = "androidx.annotation:annotation:$android_x_annotations"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${constraint_layout}"
        const val coordinatorLayout =
            "androidx.coordinatorlayout:coordinatorlayout:$android_x_coordinator"
        const val fragments = "androidx.fragment:fragment-ktx:$android_x_fragments"
        const val webview = "androidx.webkit:webkit:$android_x_webview"

        object Lifecycle {
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle"
            const val java8 = "androidx.lifecycle:lifecycle-common-java8:$lifecycle"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle"
            const val service = "androidx.lifecycle:lifecycle-service:$lifecycle"
            const val process = "androidx.lifecycle:lifecycle-process:$lifecycle"
        }

        object Room {
            const val core = "androidx.room:room-runtime:$room"
            const val coroutines = "androidx.room:room-ktx:$room"
            const val kapt = "androidx.room:room-compiler:$room"
            const val test = "androidx.room:room-testing:$room"
        }

        object WorkManager {
            const val core = "androidx.work:work-runtime-ktx:${workManager}"
            const val test = "androidx.work:work-testing:${workManager}"
        }

    }

    // testing
    object Test {
        const val junit = "junit:junit:${Versions.junit}"
        const val mockito = "org.mockito:mockito-inline:${Versions.mockito}"
        const val mockitoKotlin =
            "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
        const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
        const val android = "androidx.test:core:$android_x_test_core"
    }

    object Firebase {
        const val core = "com.google.firebase:firebase-core:$firebase_core"
        const val analytics = "com.google.firebase:firebase-analytics:$firebase_analytics"
        const val crashlytics = "com.crashlytics.sdk.android:crashlytics:$firebase_crashlytics"
        //            perf       = "com.google.firebase:firebase-perf:${Versions.firebase_perf}"
    }

    object Network {
        const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.ok_http}"
        const val okHttpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.ok_http}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
        // TODO migrate to moshi??
        const val gson = "com.google.code.gson:gson:${Versions.gson}"
    }

    object Utils {
        const val sqlContentResolver =
            "com.github.ologe:android-content-resolver-SQL:$sql_content_resolver"
        const val lastFmBinding = "de.u-mass:lastfm-java:$last_fm_binding"
        const val aesCrypto = "com.github.tozny:java-aes-crypto:$aes_crypto"
        const val fuzzy = "me.xdrop:fuzzywuzzy:$fuzzywuzzy"
        const val scrollHelper = "com.github.ologe:scroll-helper:$scroll_helper"
        const val colorDesaturation = "com.github.ologe:color-desaturation:$color_desaturation"
        const val jaudiotagger = "net.jthink:jaudiotagger:${Versions.jaudiotagger}"
    }

    object Debug {
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:$leak_canary"
        const val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
        const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
        const val chucker = "com.github.ChuckerTeam.Chucker:library:${Versions.chucker}"
        const val chuckerNoOp = "com.github.ChuckerTeam.Chucker:library-no-op:${Versions.chucker}"
    }

    object UX {
        const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
        const val glideKapt = "com.github.bumptech.glide:compiler:${Versions.glide}"
        const val blurKit = "io.alterac.blurkit:blurkit:$blur_kit"
        const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"
        const val customTabs = "saschpe.android:customtabs:$custom_tabs"
        const val dialogs = "com.afollestad.material-dialogs:color:$material_dialogs"
        const val tapTargetView = "com.github.ologe:taptargetview:$tap_target_view"
    }

    object Lint {
        val core = "com.android.tools.lint:lint-api:$lint"
        val checks = "com.android.tools.lint:lint-checks:$lint"
    }

}
