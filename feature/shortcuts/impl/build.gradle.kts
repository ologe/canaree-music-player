plugins {
    id("dev.msc.library")
    id("dev.msc.hilt")
}

dependencies {
    api(projects.feature.shortcuts.api)
    implementation(projects.feature.media.api)

    implementation(projects.common.localization)
    implementation(projects.common.shared)
    implementation(projects.common.platform)
    implementation(projects.common.ui)

    implementation(projects.core)
    implementation(projects.intents)
    implementation(projects.imageProvider)

    implementation(libs.androidx.appcompat)
    implementation(libs.ui.glide.core)
}