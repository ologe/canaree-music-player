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

    implementation(project(":core"))
    implementation(project(":shared"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)
    implementation(Libraries.Coroutines.android)

    implementation(Libraries.Dagger.core)
    kapt(Libraries.Dagger.kapt)

    implementation(Libraries.X.core)
    implementation(Libraries.Firebase.analytics)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)

}
