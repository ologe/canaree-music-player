plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply(from = rootProject.file("buildscripts/android-defaults.gradle"))

dependencies {
    implementation(projects.core)
    implementation(projects.common.shared)
    implementation(projects.common.platform)
    implementation(projects.common.localization)
    implementation(projects.common.ui)

    // todo remove dependency
    implementation(projects.features.settings.api)

    implementation(libs.androidx.preference)

    implementation(libs.ui.colorDesaturation)
}