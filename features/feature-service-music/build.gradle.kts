plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    applyDefaults()

    defaultConfig {
        configField("LAST_FM_KEY" to localProperties.lastFmKey)
        configField("LAST_FM_SECRET" to localProperties.lastFmSecret)
    }
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))

    implementation(project(":features:feature-app-shortcuts"))
    implementation(project(":domain"))
    implementation(project(":libraries:lib.image-loader"))
    implementation(project(":shared-android"))
    implementation(project(":shared"))
    implementation(project(":prefs-keys"))
    implementation(project(":intents"))
    implementation(project(":libraries:lib.equalizer"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.android)
    implementation(Libraries.Dagger.androidSupport)
    kapt(Libraries.Dagger.androidKapt)

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
