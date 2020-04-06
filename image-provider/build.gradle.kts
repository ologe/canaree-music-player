plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":jaudiotagger"))
    implementation(project(":domain"))
    implementation(project(":shared-android"))
    implementation(project(":shared"))
    implementation(project(":injection"))
    implementation(project(":prefs-keys"))
    implementation(project(":intents"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)

    implementation(Libraries.X.core)
    implementation(Libraries.X.preference)

    implementation(Libraries.UX.glide)
    kapt(Libraries.UX.glideKapt)

    implementation(Libraries.Utils.colorDesaturation)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
