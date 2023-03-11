plugins {
    id("dev.msc.library")
    id("dev.msc.hilt")
}

dependencies {
    api(projects.feature.widget.api)

    implementation(projects.common.localization)
    implementation(projects.common.shared)
    implementation(projects.common.platform)
    implementation(projects.common.ui)

    implementation(projects.core)
    implementation(projects.imageProvider)
    implementation(projects.intents)

    implementation(projects.feature.media.api)
}