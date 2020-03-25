plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {

    applyDefaults()

    buildTypes {
        val properties = localProperties

        release {
            configField("LAST_FM_KEY" to properties.lastFmKey)
            configField("LAST_FM_SECRET" to properties.lastFmSecret)
        }
        debug {
            configField("LAST_FM_KEY" to properties.lastFmKey)
            configField("LAST_FM_SECRET" to properties.lastFmSecret)
        }
    }

}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":app-shortcuts"))
    implementation(project(":jaudiotagger"))
    implementation(project(":core"))
    implementation(project(":injection"))
    implementation(project(":image-provider"))
    implementation(project(":shared-android"))
    implementation(project(":shared"))
    implementation(project(":prefs-keys"))
    implementation(project(":intents"))
    implementation(project(":equalizer"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)

    implementation(Libraries.X.media)
    implementation(Libraries.X.Lifecycle.service)
    implementation(Libraries.X.Lifecycle.java8)
    implementation(Libraries.X.Lifecycle.runtime)
    implementation(Libraries.UX.glide)

    implementation(Libraries.Utils.lastFmBinding)

    // TODO replace
    implementation("com.google.android.exoplayer:exoplayer-core:2.11.3")
//    implementation project(":exoplayer-library-core")
//    implementation project(":exoplayer-extension-flac")
//    implementation project(":exoplayer-extension-opus")
//    implementation project(":exoplayer-extension-ffmpeg")

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
