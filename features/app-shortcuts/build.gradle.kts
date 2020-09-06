plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
}

android {
    applyDefaults()
}

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":domain"))
    implementation(project(":libraries:image-loader"))
    implementation(project(":intents"))
    implementation(project(":shared"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.X.core)
    implementation(libs.X.appcompat)

    implementation(libs.UX.glide)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)
}