plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.kotlinAndroidExtensions)
    id(buildPlugins.hilt)
}

android {
    applyDefaults()
}

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

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltKapt)
    implementation(Libraries.Dagger.hiltX)
    kapt(Libraries.Dagger.hiltXKapt)

    implementation(Libraries.X.appcompat)
    implementation(Libraries.X.material)
    implementation(Libraries.X.core)
    implementation(Libraries.X.constraintLayout)
    implementation(Libraries.X.palette)
    implementation(Libraries.X.media)
    implementation(Libraries.X.browser)
    implementation(Libraries.X.preference)
    implementation(Libraries.X.coordinatorLayout)
    implementation(Libraries.X.fragments)

    implementation(Libraries.X.Lifecycle.viewmodel)
    implementation(Libraries.X.Lifecycle.java8)

    implementation(Libraries.UX.lottie)
    implementation(Libraries.UX.tapTargetView)
    implementation(Libraries.UX.dialogs)
    implementation(Libraries.UX.blurKit)
    implementation(Libraries.UX.customTabs)
    implementation(Libraries.UX.glide)

    implementation(Libraries.Utils.scrollHelper)
    implementation(Libraries.Utils.colorDesaturation)
    implementation(Libraries.Utils.lastFmBinding)
    implementation(Libraries.Utils.fuzzy)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
