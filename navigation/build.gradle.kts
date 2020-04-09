plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(Libraries.kotlin)
    implementation(Libraries.Dagger.core)

    implementation(Libraries.X.core)
    implementation(Libraries.X.fragments)
    implementation(Libraries.X.material)

    testImplementation(Libraries.Test.junit)
    testImplementation(Libraries.Test.mockito)
    testImplementation(Libraries.Test.mockitoKotlin)
    testImplementation(Libraries.Test.android)
    testImplementation(Libraries.Test.robolectric)
    testImplementation(Libraries.Coroutines.test)
}
