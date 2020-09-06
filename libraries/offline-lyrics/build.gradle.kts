plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
    id(buildPlugins.kotlinAndroidExtensions)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":shared-android"))
    implementation(project(":shared"))
    implementation(project(":intents"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)
    implementation(libs.Coroutines.android)

    implementation(libs.dagger.core)
    kapt(libs.dagger.kapt)

    implementation(libs.X.core)
    implementation(libs.X.recyclerView)
    implementation(libs.X.appcompat)
    implementation(libs.X.material)

    implementation(libs.Utils.jaudiotagger) // TODO why??

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)

}