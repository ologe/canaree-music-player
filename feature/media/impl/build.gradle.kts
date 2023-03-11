plugins {
    id("dev.msc.library")
    id("dev.msc.hilt")
}

dependencies {
    api(projects.feature.media.api)

    implementation(projects.common.localization)
    implementation(projects.common.shared)
    implementation(projects.common.platform)
    implementation(projects.common.ui)

    implementation(projects.feature.widget.api)
    implementation(projects.feature.main.api)

    implementation(projects.core)
    implementation(projects.imageProvider)
    implementation(projects.jaudiotagger)
    implementation(projects.equalizer)

    implementation(libs.androidx.media)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.utils.lastfmbinding)

    implementation(projects.exoplayerLibraryCore)
    implementation(projects.exoplayerExtensionFlac)
    implementation(projects.exoplayerExtensionOpus)
    implementation(projects.exoplayerExtensionFfmpeg)
}