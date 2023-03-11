plugins {
    id("dev.msc.library")
    id("dev.msc.hilt")
}

dependencies {
    implementation(projects.core)
    implementation(projects.common.shared)
    implementation(projects.common.platform)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.media)
    implementation(libs.androidx.lifecycle.runtime)
}