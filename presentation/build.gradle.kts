plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.kotlinAndroidExtensions)
    id(buildPlugins.hilt)
}

apply(from = rootProject.file("buildscripts/configure-android-defaults.gradle"))

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))

    implementation(project(":navigation"))
    implementation(project(":features:app-shortcuts"))
    implementation(project(":features:presentation-base"))

    // TODO temp
//    implementation(project(":feature-edit"))

    implementation(project(":libraries:analytics"))
    implementation(project(":domain"))
    implementation(project(":libraries:image-loader"))
    implementation(project(":shared-android"))
    implementation(project(":prefs-keys"))
    implementation(project(":libraries:media"))
    implementation(project(":libraries:offline-lyrics"))
    implementation(project(":intents"))
    implementation(project(":shared"))
    implementation(project(":libraries:equalizer"))
    implementation(project(":libraries:audio-tagger"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hiltKapt)
    implementation(libs.dagger.hiltX)
    kapt(libs.dagger.hiltXKapt)

    implementation(libs.X.appcompat)
    implementation(libs.X.material)
    implementation(libs.X.core)
    implementation(libs.X.constraintLayout)
    implementation(libs.X.palette)
    implementation(libs.X.media)
    implementation(libs.X.browser)
    implementation(libs.X.preference)
    implementation(libs.X.coordinatorLayout)
    implementation(libs.X.fragments)

    implementation(libs.X.Lifecycle.viewmodel)
    implementation(libs.X.Lifecycle.java8)

    implementation(libs.UX.lottie)
    implementation(libs.UX.tapTargetView)
    implementation(libs.UX.dialogs)
    implementation(libs.UX.blurKit)
    implementation(libs.UX.customTabs)
    implementation(libs.UX.glide)

    implementation(libs.Utils.scrollHelper)
    implementation(libs.Utils.colorDesaturation)
    implementation(libs.Utils.lastFmBinding)
    implementation(libs.Utils.fuzzy)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)
}
