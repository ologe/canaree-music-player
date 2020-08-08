plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
    id(BuildPlugins.hilt)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))

    implementation(project(":shared"))
    implementation(project(":prefs-keys"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)
    implementation(Libraries.Dagger.hilt)
    kapt(Libraries.Dagger.hiltKapt)

    implementation(Libraries.X.material)
    implementation(Libraries.X.core)
    implementation(Libraries.X.palette)
    implementation(Libraries.X.constraintLayout)
    implementation(Libraries.X.preference)
    implementation(Libraries.X.fragments)
    implementation(Libraries.X.media)
    implementation(Libraries.X.webview)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
