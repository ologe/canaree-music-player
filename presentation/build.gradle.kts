plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.kotlinAndroidExtensions)
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

    implementation(project(":core"))

    implementation(project(":navigation"))
    implementation(project(":feature-app-shortcuts"))
    implementation(project(":feature-presentation-base"))

    implementation(project(":analytics"))
    implementation(project(":domain"))
    implementation(project(":lib.image-loader"))
    implementation(project(":shared-android"))
    implementation(project(":prefs-keys"))
    implementation(project(":lib.media"))
    implementation(project(":lib.offline-lyrics"))
    implementation(project(":intents"))
    implementation(project(":shared"))
    implementation(project(":lib.equalizer"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.android)
    implementation(Libraries.Dagger.androidSupport)
    kapt(Libraries.Dagger.androidKapt)

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

    implementation(Libraries.Utils.jaudiotagger)
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
