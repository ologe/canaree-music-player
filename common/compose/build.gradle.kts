plugins {
    id("dev.olog.msc.library")
    id("dev.olog.msc.compose")
}

dependencies {
    implementation(projects.core)
    implementation(projects.common.shared)
    implementation(projects.common.platform)
    implementation(projects.common.localization)
    implementation(projects.common.ui)
    implementation(libs.compose.landscapist)

    // todo remove dependency
    implementation(projects.features.settings.api)

    implementation(libs.androidx.preference)

    implementation(libs.ui.colorDesaturation)
}