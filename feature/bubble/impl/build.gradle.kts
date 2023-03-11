plugins {
    id("dev.msc.library")
    id("dev.msc.hilt")
    id("kotlin-android-extensions")
}

dependencies {
    api(projects.feature.bubble.api)
    implementation(projects.feature.media.api)

    implementation(projects.core)
    implementation(projects.imageProvider)
    implementation(projects.offlineLyrics)
    implementation(projects.sharedWidgets)

    implementation(projects.common.localization)
    implementation(projects.common.shared)
    implementation(projects.common.platform)
    implementation(projects.common.ui)

    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.recycler)
    implementation(libs.androidx.media)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.ui.blurkit)
}