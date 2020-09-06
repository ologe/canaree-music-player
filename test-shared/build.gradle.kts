plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
    id(buildPlugins.kotlinKapt)
}

android {
    applyDefaults()
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.kotlin)
    implementation(libs.Coroutines.core)

    implementation(libs.Test.junit)
    implementation(libs.Coroutines.test)

}
