plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":prefs-keys"))
    implementation(project(":shared-android"))
    implementation(project(":shared"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Coroutines.core)

    implementation(Libraries.X.material)
    implementation(Libraries.X.core)
    implementation(Libraries.X.palette)

    implementation(Libraries.Debug.timber)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
