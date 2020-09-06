plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
}

apply(from = rootProject.file("buildscripts/configure-android-defaults.gradle"))

dependencies {
    implementation(project(":domain"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.Test.junit)
    implementation(libs.Coroutines.test)

}
