plugins {
    id(buildPlugins.androidLibrary)
    id(buildPlugins.kotlinAndroid)
}

apply(from = rootProject.file("buildscripts/configure-android-defaults.gradle"))
apply(from = rootProject.file("buildscripts/configure-compose.gradle"))

dependencies {
    implementation(project(":domain")) // TODO should not depend on domain
    implementation(project(":prefs-keys")) // TODO should depend on prefs-keys??
    implementation(project(":shared-android"))
    implementation(project(":libraries:image-loader"))

    compose()

    implementation(libs.X.core)
    implementation(libs.X.preference)

    implementation(libs.UX.glide)
}