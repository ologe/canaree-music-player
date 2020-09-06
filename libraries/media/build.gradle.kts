plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
}

apply(from = rootProject.file("buildscripts/configure-android-defaults.gradle"))

dependencies {
    lintChecks(project(":lint"))

    implementation(project(":core"))

    implementation(project(":domain"))

    implementation(project(":shared-android"))
    implementation(project(":intents"))
    implementation(project(":shared"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.X.media)
    implementation(libs.X.appcompat)

    implementation(libs.Debug.timber)

    testImplementation(libs.Test.junit)
    testImplementation(libs.Test.mockito)
    testImplementation(libs.Test.mockitoKotlin)
    testImplementation(libs.Test.android)
    testImplementation(libs.Test.robolectric)
    testImplementation(libs.Coroutines.test)

}
